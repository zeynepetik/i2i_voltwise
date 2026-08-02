package com.i2iacademy.voltwise.Tariff_Anomaly_Rules;

import java.math.BigDecimal;
import java.util.UUID;

public class ApplianceBreachState {
    private UUID appId;
    private int consecutiveBreaches;
    private boolean anomalyActive;
    private BigDecimal lastWattReading;

    public static ApplianceBreachState initial(UUID appId) {
        ApplianceBreachState state = new ApplianceBreachState();
        state.appId = appId;
        state.consecutiveBreaches = 0;
        state.anomalyActive = false;
        state.lastWattReading = BigDecimal.ZERO;
        return state;
    }

    public ApplianceBreachState incremented() {
        return copyWith(this.consecutiveBreaches + 1, this.anomalyActive, this.lastWattReading);
    }

    public ApplianceBreachState reset() {
        return copyWith(0, this.anomalyActive, this.lastWattReading);
    }

    public ApplianceBreachState withAnomalyActive(boolean active) {
        return copyWith(this.consecutiveBreaches, active, this.lastWattReading);
    }

    public ApplianceBreachState withLastWattReading(BigDecimal watt) {
        return copyWith(this.consecutiveBreaches, this.anomalyActive, watt);
    }

    private ApplianceBreachState copyWith(int breaches, boolean active, BigDecimal watt) {
        ApplianceBreachState next = new ApplianceBreachState();
        next.appId = this.appId;
        next.consecutiveBreaches = breaches;
        next.anomalyActive = active;
        next.lastWattReading = watt;
        return next;
    }

    public UUID getAppId() { return appId; }
    public int getConsecutiveBreaches() { return consecutiveBreaches; }
    public boolean isAnomalyActive() { return anomalyActive; }
    public BigDecimal getLastWattReading() { return lastWattReading; }
}
