package com.i2iacademy.voltwise.AI_Notification;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.i2iacademy.voltwise.AI_Notification.DTO.GeminiRequest;
import com.i2iacademy.voltwise.AI_Notification.DTO.GeminiResponse;

@Component
public class GeminiClient {
    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private static final String FALLBACK_ADVICE =
        "Bu ay enerji tüketiminiz belirlenen kotayı aştı. " +
        "Yüksek tüketimli cihazlarınızı kontrol etmenizi öneririz. " +
        "(Not: Kişiselleştirilmiş tavsiye şu anda oluşturulamadı.)";

    private final RestClient restClient;
    private final String apiKey;

    public GeminiClient(
            @Value("${voltwise.ai.gemini.api-key}") String apiKey,
            @Value("${voltwise.ai.gemini.api-url}") String baseUrl
    ) {
        this.apiKey = apiKey;

        HttpClientSettings settings = HttpClientSettings.defaults()
            .withConnectTimeout(Duration.ofSeconds(3))
            .withReadTimeout(Duration.ofSeconds(15));

        ClientHttpRequestFactory requestFactory =
            ClientHttpRequestFactoryBuilder.detect().build(settings);

        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build();
    }

    public String generateAdvice(String prompt) {
        try {
            GeminiRequest request = GeminiRequest.of(prompt);

            GeminiResponse response = restClient.post()
                .header("x-goog-api-key", apiKey)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

            String text = extractText(response);
            if (text == null || text.isBlank()) {
                log.warn("Gemini boş yanıt döndü, fallback kullanılıyor.");
                return FALLBACK_ADVICE;
            }
            return text;

        } catch (Exception e) {
            log.error("Gemini API çağrısı başarısız, fallback metni kullanılıyor.", e);
            return FALLBACK_ADVICE;
        }
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return null;
        }
        var content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }
        return content.parts().get(0).text();
    }

}
