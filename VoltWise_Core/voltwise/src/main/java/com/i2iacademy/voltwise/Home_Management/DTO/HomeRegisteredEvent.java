/*kafkaya publish edilecek event ve telemetry sonsöre ev ve appliance datalrını gönderitor */
package com.i2iacademy.voltwise.Home_Management.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record HomeRegisteredEvent(
    UUID homeId, String homeName, List<ApplianceEventDTO> appliances
) {
    public record ApplianceEventDTO(
        UUID appId, String appName, BigDecimal safeLimitWatt
    ){}

}
