package com.hiperium.city.tasks.events.utils;

import com.hiperium.city.tasks.events.model.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static void verifyIfTableIsCreated(final DynamoDbClient dynamoDbClient) {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(Event.TABLE_NAME).build();
        DynamoDbWaiter dynamoDbWaiter = dynamoDbClient.waiter();
        WaiterResponse<DescribeTableResponse> waiterResponse = dynamoDbWaiter.waitUntilTableExists(describeTableRequest);
        if (waiterResponse.matched().response().isPresent()) {
            TableDescription table = waiterResponse.matched().response().get().table();
            log.info("Table Status: {}", table.tableStatus());
            if (!table.tableStatus().equals(TableStatus.ACTIVE)) {
                throw new IllegalArgumentException("Table is not in ACTIVE state.");
            }
        } else {
            throw new IllegalArgumentException("Table does not exist.");
        }
    }
}
