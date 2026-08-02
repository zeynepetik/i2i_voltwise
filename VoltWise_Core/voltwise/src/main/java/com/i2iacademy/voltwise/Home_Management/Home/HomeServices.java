package com.i2iacademy.voltwise.Home_Management.Home;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.i2iacademy.voltwise.AI_Notification.Ai_Advice.AiAdviceRepository;
import com.i2iacademy.voltwise.AI_Notification.DTO.AiAlertResponse;
import com.i2iacademy.voltwise.Home_Management.Billing_Quota.BillingQuota;
import com.i2iacademy.voltwise.Home_Management.Billing_Quota.BillingQuotaRepository;
import com.i2iacademy.voltwise.Home_Management.Consumption_Log.ConsumptionLogRepository;
import com.i2iacademy.voltwise.Home_Management.DTO.ApplianceLiveResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.ConsumptionSnapshotResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeRegisteredEvent;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeRegistrationRequest;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeStatusResponse;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeSummaryResponse;
import com.i2iacademy.voltwise.Home_Management.TariffState;
import com.i2iacademy.voltwise.Tariff_Anomaly_Rules.ApplianceBreachState;
import com.i2iacademy.voltwise.common.HomeNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServices {
    private final HomeRepository homeRepository;
    private final BillingQuotaRepository billingQuotaRepository;
    private final ApplianceRepository applianceRepository;
    private final ConsumptionLogRepository consumptionLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final IgniteClient igniteClient;
    private final AiAdviceRepository aiAdviceRepository;
    /*value ymldeki değerleri bağlıyor */
    @Value("${voltwise.kafka.topics.registration}")
    private String registrationTopic;

    private Home toHomeEntity(HomeRegistrationRequest request) {
        Home home = new Home();
        home.setHomeName(request.homeName());
        home.setHomeAddress(request.homeAddress());
        home.setEmail(request.email());
        home.setBaseTariffRate(request.baseTariffRate());
        home.setPenaltyTariffRate(request.penaltyTariffRate());
        return home;
    }

    private List<Appliance> toApplianceEntities(HomeRegistrationRequest request, Home home) {
        return request.appliances().stream()
                .map(dto -> {
                    Appliance appliance = new Appliance();
                    appliance.setHome(home);
                    appliance.setAppName(dto.appName());
                    appliance.setAppCategory(dto.appCategory());
                    appliance.setSafeLimitWatt(dto.safeLimitWatt());
                    return appliance;
                })
                .toList();
    }

    private HomeRegisteredEvent toEvent(Home home, List<Appliance> appliances) {
        List<HomeRegisteredEvent.ApplianceEventDTO> applianceEvents = appliances.stream()
                .map(a -> new HomeRegisteredEvent.ApplianceEventDTO(a.getAppId(), a.getAppName(), a.getSafeLimitWatt()))
                .toList();
        return new HomeRegisteredEvent(home.getHomeId(), home.getHomeName(), applianceEvents);
    }

    private BillingQuota toInitialBillingQuota(HomeRegistrationRequest request, Home home) {
        LocalDate start = LocalDate.now();
        BillingQuota quota = new BillingQuota();
        quota.setHome(home);
        quota.setPowerQuota(request.powerQuota());
        quota.setBillQuota(request.billQuota());
        quota.setTariffState(TariffState.NORMAL);
        quota.setPeriodStart(start);
        quota.setPeriodEnd(start.plusMonths(1));
        return quota;
    }

    @Transactional
    public HomeResponse registerHome(HomeRegistrationRequest request) {
        Home home = homeRepository.save(toHomeEntity(request));
        List<Appliance> appliances = applianceRepository.saveAll(toApplianceEntities(request, home));
        BillingQuota quota = billingQuotaRepository.save(toInitialBillingQuota(request, home));

        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");
        cache.put(home.getHomeId(), HomeLiveState.initial(home, quota));

        HomeRegisteredEvent event = toEvent(home, appliances);
        kafkaTemplate.send(registrationTopic, home.getHomeId().toString(), event)
            .whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka publish failed for home {}", home.getHomeId(), ex);
            } else {
                log.info("Kafka publish succeeded, topic={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset());
            }
        });
        return HomeResponse.from(home, appliances, quota);
    }

    public HomeStatusResponse getLiveStatus(UUID homeId) {
        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");
        HomeLiveState state = cache.get(homeId);
        if (state == null) {
            throw new HomeNotFoundException(homeId);
        }
        return HomeStatusResponse.from(state);
    }

    public List<ConsumptionSnapshotResponse> getConsumptionHistory(UUID homeId) {
        return consumptionLogRepository.findByHome_HomeIdOrderBySnapshotDateAsc(homeId)
                .stream()
                .map(ConsumptionSnapshotResponse::from)
                .toList();
    }
    public List<ApplianceLiveResponse> getLiveAppliances(UUID homeId) {
        List<Appliance> appliances = applianceRepository.findByHome_HomeId(homeId);
        ClientCache<UUID, ApplianceBreachState> cache = igniteClient.getOrCreateCache("applianceBreachState");

        return appliances.stream()
            .map(appliance -> {
                ApplianceBreachState state = cache.get(appliance.getAppId());
                BigDecimal currentWatt = state != null ? state.getLastWattReading() : BigDecimal.ZERO;
                boolean anomalyActive = state != null && state.isAnomalyActive();
                int breaches = state != null ? state.getConsecutiveBreaches() : 0;
                boolean isBreach = currentWatt.compareTo(appliance.getSafeLimitWatt()) > 0;

                String status;
                if (anomalyActive) {
                    status = "critical";
                } else if (isBreach) {
                    status = "warning";
                } else {
                    status = "normal";
                }

                return new ApplianceLiveResponse(
                    appliance.getAppId(),
                    appliance.getAppName(),
                    appliance.getAppCategory(),
                    appliance.getSafeLimitWatt(),
                    currentWatt,
                    status,
                    breaches
                );
            })
            .toList();
    }
    public List<HomeSummaryResponse> getAllHomesSummary() {
        List<Home> homes = homeRepository.findAll();
        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");

        return homes.stream()
            .map(home -> {
                HomeLiveState state = cache.get(home.getHomeId());
                if (state == null) {
                    // Henüz Ignite'a yazılmamış (teorik olarak registerHome her zaman yazıyor, ama savunmacı davranalım)
                    return new HomeSummaryResponse(
                        home.getHomeId(), home.getHomeName(), home.getHomeAddress(),
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "NORMAL"
                    );
                }
                return new HomeSummaryResponse(
                    home.getHomeId(),
                    home.getHomeName(),
                    home.getHomeAddress(),
                    state.getCumulativeUsageKwh(),
                    state.getPowerQuota(),
                    state.getCumulativeUsageKwh(),
                    state.getCumulativeBillingAmount(),
                    state.getBillQuota(),
                    state.getTariffState()
                );
            })
            .toList();
    }

    public List<AiAlertResponse> getAiAlerts(UUID homeId) {
        return aiAdviceRepository.findByHomeIdOrderByCreatedAtDesc(homeId).stream()
            .map(advice -> new AiAlertResponse(advice.getAdviceText(), advice.getCreatedAt()))
            .toList();
    }
}
