package com.i2iacademy.voltwise.common.config;

import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import com.i2iacademy.voltwise.Telemetry_Processing.DTO.ApplianceTelemetryEvent;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, ApplianceTelemetryEvent> telemetryConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "voltwise-core-telemetry-processing-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonDeserializer<ApplianceTelemetryEvent> deserializer =
                new JsonDeserializer<>(ApplianceTelemetryEvent.class, mapper);
        deserializer.ignoreTypeHeaders(); // telemetry-sensors'ın kendi __TypeId__ header'ını görmezden gel

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ApplianceTelemetryEvent> telemetryListenerContainerFactory(
            ConsumerFactory<String, ApplianceTelemetryEvent> telemetryConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ApplianceTelemetryEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(telemetryConsumerFactory);
        return factory;
    }
}
