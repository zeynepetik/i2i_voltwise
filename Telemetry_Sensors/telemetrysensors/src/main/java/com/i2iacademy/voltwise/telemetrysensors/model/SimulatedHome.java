/*dto içinde değil çünkü dtodakiler core ile olan ilişki sözleşmlerini içeren bir halde şu an, sızdırmayı önleme çabası */
package com.i2iacademy.voltwise.telemetrysensors.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SimulatedHome(UUID homeId, String homeName, List<SimulatedAppliance> appliances) {
 public record SimulatedAppliance(UUID appId, String appName, BigDecimal safeLimitWatt){}
}
