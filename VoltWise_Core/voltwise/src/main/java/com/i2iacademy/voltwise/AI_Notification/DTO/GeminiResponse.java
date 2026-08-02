package com.i2iacademy.voltwise.AI_Notification.DTO;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {
    public record Candidate(Content content) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}
