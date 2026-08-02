package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.i2iacademy.voltwise.Home_Management.Billing_Quota.BillingQuota;
import com.i2iacademy.voltwise.Home_Management.DTO.HomeResponse.ApplianceResponse;
import com.i2iacademy.voltwise.Home_Management.Home.Appliance;
import com.i2iacademy.voltwise.Home_Management.Home.Home;

public record HomeResponse(UUID homeId,
        String homeName,
        String email,
        BigDecimal powerQuota,
        BigDecimal billQuota,
        List<ApplianceResponse> appliances) {
            
    public record ApplianceResponse(UUID appId, String appName, BigDecimal safeLimitWatt){}

    public static HomeResponse from(Home home, List<Appliance> appliances, BillingQuota quota) {
        List<ApplianceResponse> applianceResponses = appliances.stream()
                .map(a -> new ApplianceResponse(a.getAppId(), a.getAppName(), a.getSafeLimitWatt()))
                .toList();
        return new HomeResponse(
                home.getHomeId(),
                home.getHomeName(),
                home.getEmail(),
                quota.getPowerQuota(),
                quota.getBillQuota(),
                applianceResponses
        );
    }
}
