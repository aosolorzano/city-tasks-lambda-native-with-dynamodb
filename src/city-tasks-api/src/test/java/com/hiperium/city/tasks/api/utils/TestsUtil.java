package com.hiperium.city.tasks.api.utils;


import com.hiperium.city.tasks.api.model.Device;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.time.Duration;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static final String KEYCLOAK_REALM = "realms/master";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String QUARTZ_DS_PROVIDER = "hikaricp";

    public static void waitForTableToBecomeActive(final DynamoDbAsyncTable<Device> dynamoDbAsyncTable) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    dynamoDbAsyncTable.scan(ScanEnhancedRequest.builder()
                                    .limit(1)
                                    .build())
                            .items()
                            .subscribe(device -> log.debug("Devices table is ready"));
                });
    }
}
