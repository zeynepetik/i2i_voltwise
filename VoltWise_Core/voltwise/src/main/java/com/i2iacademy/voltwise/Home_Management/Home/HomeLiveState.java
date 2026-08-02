package com.i2iacademy.voltwise.Home_Management.Home;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import  com.i2iacademy.voltwise.Home_Management.Billing_Quota.BillingQuota;

public class HomeLiveState {
    private UUID homeId;
    private BigDecimal cumulativeUsageKwh;
    private BigDecimal cumulativeBillingAmount;
    private BigDecimal powerQuota;
    private BigDecimal billQuota;
    private BigDecimal baseTariffRate;
    private BigDecimal penaltyTariffRate;
    private String tariffState;      // "NORMAL" / "PENALTY" — DDL'deki CHECK ile birebir
    private Instant lastUpdated;

    public static HomeLiveState initial(Home home, BillingQuota quota) {
        HomeLiveState state = new HomeLiveState();
        state.homeId = home.getHomeId();
        state.cumulativeUsageKwh = BigDecimal.ZERO;
        state.cumulativeBillingAmount = BigDecimal.ZERO;
        state.powerQuota = quota.getPowerQuota();
        state.billQuota = quota.getBillQuota();
        state.baseTariffRate = home.getBaseTariffRate();
        state.penaltyTariffRate = home.getPenaltyTariffRate();
        state.tariffState = "NORMAL";
        state.lastUpdated = Instant.now();
        return state;
    }

    public UUID getHomeId() {
        return homeId;
    }

    public BigDecimal getCumulativeUsageKwh() {
        return cumulativeUsageKwh;
    }

    public BigDecimal getCumulativeBillingAmount() {
        return cumulativeBillingAmount;
    }

    public BigDecimal getPowerQuota() {
        return powerQuota;
    }

    public BigDecimal getBillQuota() {
        return billQuota;
    }

    public BigDecimal getBaseTariffRate() {
        return baseTariffRate;
    }

    public BigDecimal getPenaltyTariffRate() {
        return penaltyTariffRate;
    }

    public String getTariffState() {
        return tariffState;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public HomeLiveState withIncrementedUsage(BigDecimal watt, Instant recordedAt) {
        HomeLiveState next = new HomeLiveState();
        next.homeId = this.homeId;
        next.powerQuota = this.powerQuota;
        next.billQuota = this.billQuota;
        next.baseTariffRate = this.baseTariffRate;
        next.penaltyTariffRate = this.penaltyTariffRate;
        next.tariffState = this.tariffState;

        if (this.lastUpdated != null && recordedAt.isAfter(this.lastUpdated)) {
            // watt anlık güç; enerji = güç x geçen süre. Saat biriminde
            BigDecimal elapsedHours = BigDecimal.valueOf(Duration.between(this.lastUpdated, recordedAt).toMillis())
                    .divide(BigDecimal.valueOf(3_600_000), 10, RoundingMode.HALF_UP);

            BigDecimal usageKwh = watt.divide(BigDecimal.valueOf(1000), 10, RoundingMode.HALF_UP)
                    .multiply(elapsedHours);

            next.cumulativeUsageKwh = this.cumulativeUsageKwh.add(usageKwh);
            next.cumulativeBillingAmount = this.cumulativeBillingAmount.add(usageKwh.multiply(this.baseTariffRate));
        } else {
            // recordedAt geçmişte kalmış/aynı anda gelmiş bir mesaj — enerji eklemeden state'i koru.
            next.cumulativeUsageKwh = this.cumulativeUsageKwh;
            next.cumulativeBillingAmount = this.cumulativeBillingAmount;
        }

        next.lastUpdated = recordedAt;
        return next;
    }

    public HomeLiveState withTariffState(String tariffState) {
        HomeLiveState next = new HomeLiveState();
        next.homeId = this.homeId;
        next.cumulativeUsageKwh = this.cumulativeUsageKwh;
        next.cumulativeBillingAmount = this.cumulativeBillingAmount;
        next.powerQuota = this.powerQuota;
        next.billQuota = this.billQuota;
        next.baseTariffRate = this.baseTariffRate;
        next.penaltyTariffRate = this.penaltyTariffRate;
        next.tariffState = tariffState;
        next.lastUpdated = this.lastUpdated;
        return next;
    }
}
