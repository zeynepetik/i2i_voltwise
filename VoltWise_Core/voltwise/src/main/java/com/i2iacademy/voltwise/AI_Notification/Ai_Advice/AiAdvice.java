package com.i2iacademy.voltwise.AI_Notification.Ai_Advice;
/*HomeLiveState ve ApplianceBreachState'in immutable olmasının sebebi, 
onların Ignite'ta CAS retry loop'uyla güncellenmesiydi — 
concurrent erişim orada gerçek bir risk. AiAdvice ise JPA entity'si, Hibernate'in kendi dirty-checking / persistence context mekanizmasıyla yönetiliyor; 
burada immutable-kopya deseni uygulamak hem gereksiz hem de standart JPA pratiğine aykırı olurdu */
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name="ai_advice")
public class AiAdvice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_id")
    private UUID aiId;

    @Column(name = "home_id", nullable = false)
    private UUID homeId;

    @Column(name = "log_id", nullable = false)
    private UUID logId;

    @Column(name = "advice_text", nullable = false, columnDefinition = "TEXT")
    private String adviceText;

    @Column(name = "email_dispatched", nullable = false)
    private boolean emailDispatched;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected AiAdvice() {}

    public AiAdvice(UUID homeId, UUID logId, String adviceText) {
        this.homeId = homeId;
        this.logId = logId;
        this.adviceText = adviceText;
        this.createdAt = OffsetDateTime.now();
        this.emailDispatched = false;
    }

    public void markDispatched() {
        this.emailDispatched = true;
    }

    public UUID getAiId() { return aiId; }
    public UUID getHomeId() { return homeId; }
    public UUID getLogId() { return logId; }
    public String getAdviceText() { return adviceText; }
    public boolean isEmailDispatched() { return emailDispatched; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
