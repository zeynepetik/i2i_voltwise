package com.i2iacademy.voltwise.AI_Notification.DTO;

import java.time.OffsetDateTime;

public record AiAlertResponse(String adviceText,
    OffsetDateTime createdAt) {

}
