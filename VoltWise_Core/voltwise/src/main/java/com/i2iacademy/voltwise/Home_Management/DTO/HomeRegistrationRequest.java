package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HomeRegistrationRequest(@NotBlank @Size(max = 50) String homeName,
        @NotBlank @Size(max = 150) String homeAddress,
        @NotBlank @Email @Size(max = 50) String email,
        @NotNull @Positive BigDecimal baseTariffRate,
        @NotNull @Positive BigDecimal penaltyTariffRate,
        @NotNull @Positive BigDecimal powerQuota,   // kWh — billing_quota.power_quota
        @NotNull @Positive BigDecimal billQuota,    // TL — billing_quota.bill_quota
        @NotEmpty List<ApplianceDTO> appliances) {
}
