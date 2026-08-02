package com.i2iacademy.voltwise.Telemetry_Processing;

import java.util.UUID;

import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.i2iacademy.voltwise.Home_Management.Home.HomeLiveState;
import com.i2iacademy.voltwise.Tariff_Anomaly_Rules.TariffAnomalyEvaluator;
import com.i2iacademy.voltwise.Telemetry_Processing.DTO.ApplianceTelemetryEvent;

@Component
public class TelemetryProcessingListener {
    private static final Logger log = LoggerFactory.getLogger(TelemetryProcessingListener.class);
    private static final int MAX_CAS_ATTEMPTS = 5;

    private final IgniteClient igniteClient;
    /* previous stati evaluatora aktarma için gerekli*/
    private final TariffAnomalyEvaluator tariffAnomalyEvaluator;

    public TelemetryProcessingListener(IgniteClient igniteClient, TariffAnomalyEvaluator tariffAnomalyEvaluator) {
        this.igniteClient = igniteClient;
        this.tariffAnomalyEvaluator=tariffAnomalyEvaluator;
    }

    @KafkaListener(
            topics = "${voltwise.kafka.topics.telemetry}",
            containerFactory = "telemetryListenerContainerFactory"
    )
    public void onApplianceTelemetry(ApplianceTelemetryEvent event) {
        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");
        UUID homeId = event.homeId();

        for (int attempt = 1; attempt <= MAX_CAS_ATTEMPTS; attempt++) {
            HomeLiveState current = cache.get(homeId);
            if (current == null) {
                log.warn("Telemetry geldi ama Ignite'ta home bulunamadı: homeId={}", homeId);
                return;
            }

            HomeLiveState updated = current.withIncrementedUsage(event.watt(), event.recordedAt());

            if (cache.replace(homeId, current, updated)) {
                tariffAnomalyEvaluator.evaluate(current, updated, event); 
                return;
            }
            // replace false döndü: araya başka bir thread girdi, state güncel değil — tekrar dene
            log.debug("CAS çakışması, tekrar deneniyor: homeId={}, attempt={}", homeId, attempt);
        }

        log.error("CAS retry limiti aşıldı, telemetry işlenemedi: homeId={}, appId={}", homeId, event.appId());
    }
}
