package com.i2iacademy.voltwise.telemetrysensors.registry;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.i2iacademy.voltwise.telemetrysensors.model.SimulatedHome;

@Component
public class SensorRegistry {
    /*Kafka consumer threadi yazıyor, @scheduledi okuyor */
    private final Map<UUID, SimulatedHome> homes=new ConcurrentHashMap<>();
    /*upsert uygulandı varsa update et yoksa insert et */
    public void register(SimulatedHome home) {
        homes.put(home.homeId(), home);
    }

    public Collection<SimulatedHome> allHomes() {
        return homes.values();
    }

    public boolean isEmpty() {
        return homes.isEmpty();
    }
}
