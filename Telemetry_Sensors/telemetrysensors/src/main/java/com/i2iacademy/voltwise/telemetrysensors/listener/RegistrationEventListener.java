package com.i2iacademy.voltwise.telemetrysensors.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.i2iacademy.voltwise.telemetrysensors.DTO.HomeRegisteredEvent;
import com.i2iacademy.voltwise.telemetrysensors.model.SimulatedHome;
import com.i2iacademy.voltwise.telemetrysensors.registry.SensorRegistry;

@Component
public class RegistrationEventListener {
    private static final Logger log= LoggerFactory.getLogger(RegistrationEventListener.class);
    private final SensorRegistry registry;

    public RegistrationEventListener(SensorRegistry registry) {
        this.registry = registry;
    }

    @KafkaListener(
            topics = "${voltwise.kafka.topics.registration}",
            groupId = "voltwise-telemetry-sensors-group"
    )
    public void onHomeRegistered(HomeRegisteredEvent event) {
        List<SimulatedHome.SimulatedAppliance> appliances = event.appliances().stream()
                .map(a -> new SimulatedHome.SimulatedAppliance(a.appId(), a.appName(), a.safeLimitWatt()))
                .toList();

        registry.register(new SimulatedHome(event.homeId(), event.homeName(), appliances));

        log.info("Home simülasyona eklendi: {} ({} appliance)", event.homeName(), appliances.size());
    }
}
