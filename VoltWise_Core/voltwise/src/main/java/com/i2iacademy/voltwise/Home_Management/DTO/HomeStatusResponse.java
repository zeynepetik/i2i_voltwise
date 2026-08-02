package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.i2iacademy.voltwise.Home_Management.Home.HomeLiveState;

public record HomeStatusResponse(UUID homeId,
        BigDecimal cumulativeUsageKwh,
        BigDecimal cumulativeBillingAmount,
        BigDecimal powerQuota,
        BigDecimal billQuota,
        String tariffState,
        Instant lastUpdated) {
    
    public static HomeStatusResponse from(HomeLiveState state) {
        return new HomeStatusResponse(
                state.getHomeId(),
                state.getCumulativeUsageKwh(),
                state.getCumulativeBillingAmount(),
                state.getPowerQuota(),
                state.getBillQuota(),
                state.getTariffState(),
                state.getLastUpdated()
        );
    }
}
