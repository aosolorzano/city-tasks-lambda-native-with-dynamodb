package com.hiperium.city.tasks.events.utils;

import com.hiperium.city.tasks.events.model.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationResponse;
import software.amazon.awssdk.services.lambda.model.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static final String FUNCTION_NAME = "city-tasks-events-function";

    public static void verifyIfTableIsCreated(final DynamoDbClient dynamoDbClient) {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(Event.TABLE_NAME).build();
        DynamoDbWaiter dynamoDbWaiter = dynamoDbClient.waiter();
        WaiterResponse<DescribeTableResponse> waiterResponse = dynamoDbWaiter.waitUntilTableExists(describeTableRequest);
        if (waiterResponse.matched().response().isPresent()) {
            TableDescription table = waiterResponse.matched().response().get().table();
            log.debug("Table Status: {}", table.tableStatus());
            if (!table.tableStatus().equals(TableStatus.ACTIVE)) {
                throw new IllegalArgumentException("Table is not in ACTIVE state.");
            }
        } else {
            throw new IllegalArgumentException("Table does not exist.");
        }
    }

    public static Callable<Boolean> verifyLambdaActiveState(LambdaClient lambdaClient) {
        return () -> {
            GetFunctionConfigurationRequest getRequest = GetFunctionConfigurationRequest.builder()
                    .functionName(FUNCTION_NAME)
                    .build();
            GetFunctionConfigurationResponse getResponse = lambdaClient.getFunctionConfiguration(getRequest);
            return getResponse.stateAsString().equals(State.ACTIVE.toString());
        };
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
