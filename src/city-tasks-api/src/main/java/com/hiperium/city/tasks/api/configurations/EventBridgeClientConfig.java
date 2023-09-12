package com.hiperium.city.tasks.api.configurations;

import com.hiperium.city.tasks.api.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Configuration
public class EventBridgeClientConfig {

    private final Environment environment;

    public EventBridgeClientConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public EventBridgeAsyncClient eventBridgeAsyncClient() {
        var eventBridgeClientBuilder = EventBridgeAsyncClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build());
        String endpointOverrideURL = this.environment.getProperty(PropertiesUtil.AWS_ENDPOINT_OVERRIDE_PROPERTY);
        if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isBlank()) {
            log.debug("EventBridge Endpoint Override: {}", endpointOverrideURL);
            eventBridgeClientBuilder.endpointOverride(URI.create(endpointOverrideURL));
        }
        return eventBridgeClientBuilder.build();
    }
}
