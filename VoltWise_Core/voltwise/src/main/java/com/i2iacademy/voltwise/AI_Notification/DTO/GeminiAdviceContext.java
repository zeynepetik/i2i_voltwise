package com.i2iacademy.voltwise.AI_Notification.DTO;

import java.util.List;
import java.util.UUID;

public record GeminiAdviceContext(UUID homeId,
    double cumulativeUsageKwh,
    double quotaLimitKwh,
    String tariffState,          // "NORMAL" / "PENALTY"
    List<String> recentSystemLogEvents /*80quotabreach, 100 quotabreach etc */) {

}
