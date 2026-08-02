package com.i2iacademy.voltwise.AI_Notification.Ai_Advice;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAdviceRepository extends JpaRepository<AiAdvice, UUID>{
    List<AiAdvice> findByHomeIdOrderByCreatedAtDesc(UUID homeId);
}