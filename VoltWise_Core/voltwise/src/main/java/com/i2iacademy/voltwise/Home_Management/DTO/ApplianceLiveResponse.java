package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplianceLiveResponse(UUID appId,
    String appName,
    String appCategory,
    BigDecimal safeLimitWatt,
    BigDecimal currentWatt,
    String status,          // "normal" | "warning" | "critical"
    int consecutiveBreaches) {}
