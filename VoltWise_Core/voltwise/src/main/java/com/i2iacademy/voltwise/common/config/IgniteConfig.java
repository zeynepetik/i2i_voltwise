package com.i2iacademy.voltwise.common.config;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteConfig {
    @Value("${voltwise.ignite.host}")
    private String host;

    @Value("${voltwise.ignite.port}")
    private int port;

    @Bean(destroyMethod = "close")
    public IgniteClient igniteClient() {
        ClientConfiguration cfg = new ClientConfiguration()
                .setAddresses(host + ":" + port);
        return Ignition.startClient(cfg);
    }
}
