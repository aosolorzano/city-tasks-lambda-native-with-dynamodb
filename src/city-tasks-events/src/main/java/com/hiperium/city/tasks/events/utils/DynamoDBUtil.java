package com.hiperium.city.tasks.events.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DynamoDBUtil {

    public static final String AWS_ENDPOINT_OVERRIDE_PROPERTY = "aws.endpoint-override";

    public static DynamoDbClient getDynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build());
        String endpointOverrideURL = System.getProperty(AWS_ENDPOINT_OVERRIDE_PROPERTY);
        log.debug("DynamoDB Endpoint Override: {}", endpointOverrideURL);
        if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isEmpty()) {
            builder.endpointOverride(URI.create(endpointOverrideURL));
        }
        return builder.build();
    }
}
