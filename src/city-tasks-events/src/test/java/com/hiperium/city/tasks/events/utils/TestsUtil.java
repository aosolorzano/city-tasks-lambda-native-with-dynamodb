package com.hiperium.city.tasks.events.utils;

import com.hiperium.city.tasks.events.model.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static final String FUNCTION_NAME = "city-tasks-events-function";

    public static void waitForTableToBeCreated(final DynamoDbAsyncClient dynamoDbAsyncClient) {
        await()
                .atMost(Duration.ofSeconds(30))         // maximum wait time
                .pollInterval(Duration.ofSeconds(10))   // check every 10 seconds
                .until(() -> {
                    CompletableFuture<DescribeTableResponse> future = dynamoDbAsyncClient
                            .describeTable(DescribeTableRequest
                                    .builder()
                                    .tableName(Event.TABLE_NAME)
                                    .build());
                    try {
                        return TableStatus.ACTIVE.equals(future.get().table().tableStatus());
                    } catch (InterruptedException | ExecutionException e) {
                        return false;
                    }
                });
    }

    public static void waitForFunctionToBeActive(LambdaClient lambdaClient) {
        await()
                .atMost(Duration.ofSeconds(30))         // maximum wait time
                .pollInterval(Duration.ofSeconds(10))   // check every 10 seconds
                .until(() -> {
                    GetFunctionConfigurationRequest request = GetFunctionConfigurationRequest.builder()
                            .functionName(FUNCTION_NAME)
                            .build();
                    State state = lambdaClient.getFunctionConfiguration(request).state();
                    return State.ACTIVE.equals(state);
                });
    }

    public static SdkBytes getFileAsSdkBytes(final String filePath) {
        Path path = Paths.get(filePath);
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            return SdkBytes.fromByteArray(fileBytes);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file: " + filePath, e);
        }
    }
}
