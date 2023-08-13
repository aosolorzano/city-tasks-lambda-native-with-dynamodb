package com.hiperium.city.tasks.api.utils;


import com.hiperium.city.tasks.api.model.Device;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbAsyncWaiter;

import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static final String KEYCLOAK_REALM = "realms/master";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String QUARTZ_DS_PROVIDER = "hikaricp";

    public static void verifyIfTableIsCreated(final DynamoDbAsyncClient dynamoDbAsyncClient) {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(Device.TABLE_NAME).build();
        DynamoDbAsyncWaiter dynamoDbAsyncWaiter = dynamoDbAsyncClient.waiter();
        CompletableFuture<WaiterResponse<DescribeTableResponse>> futureWaiterResponse = dynamoDbAsyncWaiter
                .waitUntilTableExists(describeTableRequest);

        futureWaiterResponse.thenAccept(waiterResponse -> {
            if (waiterResponse.matched().response().isPresent()) {
                TableDescription table = waiterResponse.matched().response().get().table();
                if (!table.tableStatus().equals(TableStatus.ACTIVE)) {
                    throw new RuntimeException("Table is not in ACTIVE state.");
                }
            } else {
                throw new RuntimeException("Table does not exist.");
            }
        }).join();
    }
}
