package com.hiperium.city.tasks.api.config;

import com.hiperium.city.tasks.api.utils.PropertiesLoaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.net.URI;
import java.util.Objects;

@Configuration
public class EventBridgeClientConfig {

    private final Environment environment;

    public EventBridgeClientConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public EventBridgeClient eventBridgeClient() {
        var eventBridgeClientBuilder = EventBridgeClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build());
        String endpointOverride = this.environment.getProperty(PropertiesLoaderUtil.AWS_ENDPOINT_OVERRIDE);
        if (Objects.nonNull(endpointOverride) && !endpointOverride.isBlank()) {
            eventBridgeClientBuilder.endpointOverride(URI.create(endpointOverride));
        }
        return eventBridgeClientBuilder.build();
    }
}
