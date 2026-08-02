package com.i2iacademy.voltwise.Tariff_Anomaly_Rules;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.i2iacademy.voltwise.AI_Notification.Ai_Advice.AiAdvisoryService;
import com.i2iacademy.voltwise.AI_Notification.DTO.GeminiAdviceContext;
import com.i2iacademy.voltwise.Home_Management.Home.Appliance;
import com.i2iacademy.voltwise.Home_Management.Home.ApplianceRepository;
import com.i2iacademy.voltwise.Home_Management.Home.HomeLiveState;
import com.i2iacademy.voltwise.System_Log.SystemLogEventType;
import com.i2iacademy.voltwise.System_Log.SystemLogService;
import com.i2iacademy.voltwise.Telemetry_Processing.DTO.ApplianceTelemetryEvent;

@Service
public class TariffAnomalyEvaluator {
    private static final Logger log = LoggerFactory.getLogger(TariffAnomalyEvaluator.class);

    private static final BigDecimal QUOTA_WARNING_RATIO = new BigDecimal("0.80");
    private static final BigDecimal QUOTA_BREACH_RATIO = new BigDecimal("1.00");
    private static final int BREACH_THRESHOLD_COUNT = 3;
    private static final int MAX_CAS_ATTEMPTS = 5;

    private final IgniteClient igniteClient;
    private final ApplianceRepository applianceRepository;
    private final SystemLogService systemLogService;
    private final AiAdvisoryService aiAdvisoryService;

    public TariffAnomalyEvaluator(IgniteClient igniteClient,
                                   ApplianceRepository applianceRepository,
                                   SystemLogService systemLogService, AiAdvisoryService aiAdvisoryService) {
        this.igniteClient = igniteClient;
        this.applianceRepository = applianceRepository;
        this.systemLogService = systemLogService;
        this.aiAdvisoryService=aiAdvisoryService;
    }

    /*CAS güncellemesinden önceki ve sonraki homeLiveStatei al */
    public void evaluate(HomeLiveState previous, HomeLiveState updated, ApplianceTelemetryEvent event) {
        evaluateQuota(previous, updated, event.homeId());
        evaluateApplianceBreach(updated, event);
    }

    private void evaluateQuota(HomeLiveState previous, HomeLiveState updated, UUID homeId) {
        if (updated.getPowerQuota() == null || updated.getPowerQuota().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal previousRatio = ratioOf(previous);
        BigDecimal updatedRatio = ratioOf(updated);

        // %100 eşiğini ŞİMDİ geçtiyse 
        if (previousRatio.compareTo(QUOTA_BREACH_RATIO) < 0 && updatedRatio.compareTo(QUOTA_BREACH_RATIO) >= 0) {
            UUID logId = systemLogService.log(homeId, null, SystemLogEventType.QUOTA_BREACH_100,
                    Map.of("usageRatio", updatedRatio.toString()));
            if (logId != null) {
                triggerAiAdvisory(homeId, updated, logId, "QUOTA_BREACH_100");
            }

            activatePenaltyTariff(homeId);;
        }
        // %80 eşiğini ŞİMDİ geçtiyse
        else if (previousRatio.compareTo(QUOTA_WARNING_RATIO) < 0 && updatedRatio.compareTo(QUOTA_WARNING_RATIO) >= 0) {
            UUID logId = systemLogService.log(homeId, null, SystemLogEventType.QUOTA_BREACH_80,
                    Map.of("usageRatio", updatedRatio.toString()));

            if (logId != null) {
                triggerAiAdvisory(homeId, updated, logId, "QUOTA_BREACH_80");
            }
        }
    }

    private BigDecimal ratioOf(HomeLiveState state) {
        return state.getCumulativeUsageKwh().divide(state.getPowerQuota(), 6, RoundingMode.HALF_UP);
    }

    private void activatePenaltyTariff(UUID homeId) {
        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");

        for (int attempt = 1; attempt <= MAX_CAS_ATTEMPTS; attempt++) {
            HomeLiveState current = cache.get(homeId);
            if (current == null || "PENALTY".equals(current.getTariffState())) {
                return;
            }

            HomeLiveState next = current.withTariffState("PENALTY");
            if (cache.replace(homeId, current, next)) {
                UUID logId = systemLogService.log(homeId, null, SystemLogEventType.PENALTY_TARIFF_ACTIVATED, Map.of());

                // YENİ: AI tavsiyesini tetikle
                if (logId != null) {
                    triggerAiAdvisory(homeId, next, logId, "PENALTY_TARIFF_ACTIVATED");
                }
                return;
            }
        }
        log.error("Penalty tariff CAS retry limiti aşıldı: homeId={}", homeId);
    }

    private void evaluateApplianceBreach(HomeLiveState updated, ApplianceTelemetryEvent event) {
        Appliance appliance = applianceRepository.findById(event.appId()).orElse(null);
        if (appliance == null) {
            log.warn("Appliance bulunamadı, breach kontrolü atlanıyor: appId={}", event.appId());
            return;
        }

        boolean isBreach = event.watt().compareTo(appliance.getSafeLimitWatt()) > 0;
        UUID appId = event.appId();
        ClientCache<UUID, ApplianceBreachState> cache = igniteClient.getOrCreateCache("applianceBreachState");

        for (int attempt = 1; attempt <= MAX_CAS_ATTEMPTS; attempt++) {
            ApplianceBreachState current = cache.get(appId);
            if (current == null) {
                current = ApplianceBreachState.initial(appId);
                cache.putIfAbsent(appId, current);
                continue;
            }

            ApplianceBreachState candidate = (isBreach ? current.incremented() : current.reset())
            .withLastWattReading(event.watt());

            boolean crossingIntoAnomaly = !current.isAnomalyActive()
                    && candidate.getConsecutiveBreaches() >= BREACH_THRESHOLD_COUNT;
            boolean crossingOutOfAnomaly = current.isAnomalyActive() && !isBreach;

            ApplianceBreachState next = candidate;
            if (crossingIntoAnomaly) {
                next = candidate.withAnomalyActive(true);
            } else if (crossingOutOfAnomaly) {
                next = candidate.withAnomalyActive(false);
            }

            if (cache.replace(appId, current, next)) {
                if (crossingIntoAnomaly) {
                    UUID logId = systemLogService.log(event.homeId(), appId, SystemLogEventType.DEVICE_ANOMALY_DETECTED,
                            Map.of("watt", event.watt().toString(), "safeLimitWatt", appliance.getSafeLimitWatt().toString()));

                    // YENİ: AI tavsiyesini tetikle
                    if (logId != null) {
                        triggerAiAdvisory(event.homeId(), updated, logId, "DEVICE_ANOMALY_DETECTED");
                    }
                } else if (crossingOutOfAnomaly) {
                    systemLogService.log(event.homeId(), appId, SystemLogEventType.DEVICE_ANOMALY_CLEARED, Map.of());
                    // CLEARED'da AI tetiklemiyoruz — "sorun bitti" için tavsiye maili anlamsız
                }
                return;
            }
        }
        log.error("Breach counter CAS retry limiti aşıldı: appId={}", appId);
    }

    private void triggerAiAdvisory(UUID homeId, HomeLiveState state, UUID logId, String eventLabel){
        GeminiAdviceContext context=new GeminiAdviceContext(homeId, state.getCumulativeUsageKwh().doubleValue(),
                state.getPowerQuota().doubleValue(),
                state.getTariffState(), 
                List.of(eventLabel));
        
        aiAdvisoryService.generateAndDispatchAsync(context, logId);
    }
}
