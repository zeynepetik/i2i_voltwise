package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record HomeSummaryResponse(UUID homeId,
    String homeName,
    String homeAddress,
    BigDecimal liveDrawKw,      // = cumulativeUsageKwh (bkz. not yukarıda)
    BigDecimal quotaKw,         // = powerQuota
    BigDecimal dailyUsageKwh,   // = cumulativeUsageKwh (aynı değer, basitleştirme)
    BigDecimal monthlyBill,     // = cumulativeBillingAmount
    BigDecimal billQuota,
    String tariffState) {

}
