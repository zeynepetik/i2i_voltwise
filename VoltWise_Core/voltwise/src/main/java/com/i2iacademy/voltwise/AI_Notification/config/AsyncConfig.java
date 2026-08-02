package com.i2iacademy.voltwise.AI_Notification.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "aiAdvisoryExecutor")
    public ThreadPoolTaskExecutor aiAdvisoryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-advisory-");

        // CallerRunsPolicy KULLANMIYORUZ çünkü o, çağıran thread'e (Kafka consumer'a)
        // görevi geri verir -> tam kaçınmak istediğimiz bloklama bu.
        // DiscardOldestPolicy: en eski bekleyen görevi at, yenisini kabul et.
        // eski bir anomali tavsiyesi zaten atık geresizleşmiştir, en güncel
        // duruma öncelik vermek daha anlamlı.
        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        executor.initialize();
        return executor;
    }
}
