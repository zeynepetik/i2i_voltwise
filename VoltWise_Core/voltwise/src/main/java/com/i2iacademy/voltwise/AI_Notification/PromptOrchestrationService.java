package com.i2iacademy.voltwise.AI_Notification;

import org.springframework.stereotype.Service;

import com.i2iacademy.voltwise.AI_Notification.DTO.GeminiAdviceContext;

@Service
public class PromptOrchestrationService {
    public String buildPrompt(GeminiAdviceContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("Sen bir enerji tasarrufu danışmanısın. ")
          .append("Aşağıdaki ev enerji tüketim verilerine göre, ev sahibine ")
          .append("gönderilecek KISA (en fazla 3-4 cümle), samimi ama profesyonel ")
          .append("bir Türkçe tavsiye metni yaz. Teknik jargon kullanma, ")
          .append("doğrudan eyleme geçirilebilir öneriler ver.\n\n");

        sb.append("Ev durumu:\n");
        sb.append("- Toplam tüketim: %.2f kWh\n".formatted(context.cumulativeUsageKwh()));
        sb.append("- Kota limiti: %.2f kWh\n".formatted(context.quotaLimitKwh()));
        sb.append("- Tarife durumu: %s\n".formatted(
            "PENALTY".equals(context.tariffState())
                ? "Cezalı tarife aktif (kota aşıldı)"
                : "Normal tarife"
        ));

        if (!context.recentSystemLogEvents().isEmpty()) {
            sb.append("- Son olaylar: ").append(String.join(", ", context.recentSystemLogEvents())).append("\n");
        }

        sb.append("\nSadece tavsiye metnini yaz, başka açıklama ekleme.");

        return sb.toString();
    }
}
