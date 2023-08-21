package com.hiperium.city.tasks.events.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DynamoDBUtil {

    public static DynamoDbAsyncClient getDynamoDbClient() {
        var builder = DynamoDbAsyncClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClientBuilder(AwsCrtAsyncHttpClient.builder());
        String endpointOverrideURL = getEndpointOverrideURL();
        log.debug("DynamoDB Endpoint Override: {}", endpointOverrideURL);
        if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverrideURL));
        }
        return builder.build();
    }

    private static String getEndpointOverrideURL() {
        String endpointOverrideURL = System.getProperty(FunctionUtil.AWS_ENDPOINT_OVERRIDE_PROPERTY);
        if (Objects.isNull(endpointOverrideURL) || endpointOverrideURL.isEmpty()) {
            endpointOverrideURL = System.getenv(FunctionUtil.AWS_ENDPOINT_OVERRIDE_VARIABLE);
        }
        return endpointOverrideURL;
    }
}
