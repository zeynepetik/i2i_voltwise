package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ApplianceDTO(@NotBlank @Size(max = 64) String appName,
        @NotBlank @Size(max = 32) String appCategory, 
        @NotNull @Positive BigDecimal safeLimitWatt) {
    
}
