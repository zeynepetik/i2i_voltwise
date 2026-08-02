package com.i2iacademy.voltwise.telemetrysensors.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ApplianceTelemetryEvent(UUID homeId, UUID appId, BigDecimal watt, Instant recordedAt) {

}
