package com.i2iacademy.voltwise.telemetrysensors.publisher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.i2iacademy.voltwise.telemetrysensors.DTO.ApplianceTelemetryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.i2iacademy.voltwise.telemetrysensors.model.SimulatedHome;
import com.i2iacademy.voltwise.telemetrysensors.registry.SensorRegistry;

@Component
public class TelemetryPublisher {
    private static final Logger log=LoggerFactory.getLogger(TelemetryPublisher.class);
    /*safe watt limitleri belirle %60 ve %115 arası*/
    private static final double MIN_FACTOR = 0.60;
    private static final double MAX_FACTOR = 1.15;

    private final SensorRegistry registry;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String telemetryTopic;

    public TelemetryPublisher(
            SensorRegistry registry,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${voltwise.kafka.topics.telemetry}") String telemetryTopic
    ) {
        this.registry = registry;
        this.kafkaTemplate = kafkaTemplate;
        this.telemetryTopic = telemetryTopic;
    }

    @Scheduled(fixedRateString = "${voltwise.telemetry.interval-ms:5000}")
    public void publishTelemetry() {
        if (registry.isEmpty()) {
            return; // henüz hiç home kayıtlı değil, üretecek bir şey yok
        }

        for (SimulatedHome home : registry.allHomes()) {
            for (SimulatedHome.SimulatedAppliance appliance : home.appliances()) {
                BigDecimal watt = randomWattFor(appliance.safeLimitWatt());

                ApplianceTelemetryEvent event = new ApplianceTelemetryEvent(
                        home.homeId(), appliance.appId(), watt, Instant.now()
                );

                kafkaTemplate.send(telemetryTopic, home.homeId().toString(), event)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Telemetry publish hatası: home={}, app={}", home.homeId(), appliance.appId(), ex);
                            }
                        });
            }
        }
    }

    private BigDecimal randomWattFor(BigDecimal safeLimitWatt) {
        double factor = ThreadLocalRandom.current().nextDouble(MIN_FACTOR, MAX_FACTOR);
        return safeLimitWatt.multiply(BigDecimal.valueOf(factor))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
