package com.hiperium.city.tasks.api.config;

import com.hiperium.city.tasks.api.utils.PropertiesLoaderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;
import java.util.Objects;

@Configuration
public class DynamoDBClientConfig {

    private final Environment environment;

    public DynamoDBClientConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        String endpointOverride = this.environment.getProperty(PropertiesLoaderUtil.AWS_ENDPOINT_OVERRIDE);
        var dynamoDbClientBuilder = getDynamoDbAsyncClientBuilder(endpointOverride);
        return dynamoDbClientBuilder.build();
    }

    public static DynamoDbAsyncClientBuilder getDynamoDbAsyncClientBuilder(final String endpointOverride) {
        var dynamoDbClientBuilder = DynamoDbAsyncClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build());
        if (Objects.nonNull(endpointOverride) && !endpointOverride.isBlank()) {
            dynamoDbClientBuilder.endpointOverride(URI.create(endpointOverride));
        }
        return dynamoDbClientBuilder;
    }
}
