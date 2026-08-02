package com.i2iacademy.voltwise.Telemetry_Processing.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ApplianceTelemetryEvent(UUID homeId,
        UUID appId,
        BigDecimal watt,
        Instant recordedAt) {

}
