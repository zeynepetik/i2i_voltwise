package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.i2iacademy.voltwise.Home_Management.Consumption_Log.ConsumptionLog;

public record ConsumptionSnapshotResponse(BigDecimal totalPower, BigDecimal totalCost, LocalDate snapshotDate) {
    public static ConsumptionSnapshotResponse from(ConsumptionLog log) {
        return new ConsumptionSnapshotResponse(log.getTotalPower(), log.getTotalCost(), log.getSnapshotDate());
    }
}
