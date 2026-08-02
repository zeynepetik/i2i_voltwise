package com.i2iacademy.voltwise.AI_Notification.Ai_Advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.i2iacademy.voltwise.AI_Notification.GeminiClient;
import com.i2iacademy.voltwise.AI_Notification.DTO.GeminiAdviceContext;
import com.i2iacademy.voltwise.AI_Notification.Mail.MailService;
import com.i2iacademy.voltwise.Home_Management.Home.Home;
import com.i2iacademy.voltwise.Home_Management.Home.HomeRepository;
import com.i2iacademy.voltwise.System_Log.SystemLogEventType;
import com.i2iacademy.voltwise.System_Log.SystemLogService;
import com.i2iacademy.voltwise.AI_Notification.*;
import java.util.UUID;
import java.util.Map;

@Service
public class AiAdvisoryService {
    private static final Logger log = LoggerFactory.getLogger(AiAdvisoryService.class);

    private final PromptOrchestrationService promptOrchestrationService;
    private final GeminiClient geminiClient;
    private final AiAdviceRepository aiAdviceRepository;
    private final MailService mailService;
    private final SystemLogService systemLogService;
    private final HomeRepository homeRepository;

    public AiAdvisoryService(
            PromptOrchestrationService promptOrchestrationService,
            GeminiClient geminiClient,
            AiAdviceRepository aiAdviceRepository,
            MailService mailService,
            SystemLogService systemLogService,
            HomeRepository homeRepository
    ) {
        this.promptOrchestrationService = promptOrchestrationService;
        this.geminiClient = geminiClient;
        this.aiAdviceRepository = aiAdviceRepository;
        this.mailService = mailService;
        this.systemLogService = systemLogService;
        this.homeRepository = homeRepository;
    }

    @Async("aiAdvisoryExecutor")
    public void generateAndDispatchAsync(GeminiAdviceContext context, UUID triggerLogId) {
        try {
            String prompt = promptOrchestrationService.buildPrompt(context);
            String adviceText = geminiClient.generateAdvice(prompt);

            AiAdvice advice = new AiAdvice(context.homeId(), triggerLogId, adviceText);
            aiAdviceRepository.save(advice);

            String ownerEmail = homeRepository.findById(context.homeId())
                .map(Home::getEmail)
                .orElse(null);

            if (ownerEmail == null) {
                log.warn("Home {} için email adresi bulunamadı, mail gönderilmiyor.", context.homeId());
            } else {
                boolean sent = mailService.sendAdvisoryEmail(ownerEmail, "VoltWise Enerji Tavsiyesi", adviceText);
                if (sent) {
                    advice.markDispatched();
                    aiAdviceRepository.save(advice);
                }
            }

            // AI_ADVISORY_GENERATED event'i, triggerLogId'den AYRI, yeni bir log satırı.
            // details Map'ine tetikleyici log'un id'sini koyuyoruz ki izlenebilirlik korunsun.
            systemLogService.log(
                context.homeId(),
                null,
                SystemLogEventType.AI_ADVISORY_GENERATED,
                Map.of("triggerLogId", triggerLogId.toString(), "emailDispatched", advice.isEmailDispatched())
            );

        } catch (Exception e) {
            log.error("AI tavsiye akışı home {} için başarısız oldu.", context.homeId(), e);
        }
    }
}
