package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.config.DynamoDBClientConfig;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.DeviceUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbAsyncWaiter;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceRepositoryTest extends AbstractContainerBaseTest {

    public static final String DEVICE_ID = "123";

    @Autowired
    private DeviceRepository deviceRepository;

    private static Task task;

    @BeforeAll
    public static void init() {
        DynamoDbAsyncClient dbAsyncClient = DynamoDBClientConfig
                .getDynamoDbAsyncClientBuilder(getLocalstackContainer().getEndpoint().toString())
                .build();
        createTable(dbAsyncClient);
        Device device = getNewDevice();
        dbAsyncClient.putItem(DeviceUtil.createPutRequest(device));
        task = TaskUtil.getTaskTemplate();
        task.setDeviceId(DEVICE_ID);
    }

    @Test
    @Order(1)
    @DisplayName("Find Device by ID")
    void givenDeviceId_whenFindById_mustReturnDeviceItem() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceMonoResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 1");
                    assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.ON);
                })
                .verifyComplete();
    }

    @Test
    @Order(2)
    @DisplayName("Find not existing Device ID")
    void givenDeviceId_whenFindById_mustThrowException() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById("1000");
        StepVerifier.create(deviceMonoResponse)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @Order(3)
    @DisplayName("Turn Device OFF")
    void givenDeviceItem_whenTaskTurnedOff_mustUpdateDeviceStatus() throws InterruptedException {
        task.setDeviceOperation(EnumDeviceOperation.DEACTIVATE);
        Mono<Task> deviceUpdateResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(deviceUpdateResponse)
                .assertNext(taskResponse -> {
                    assertThat(taskResponse).isNotNull();
                    assertThat(taskResponse.getId()).isEqualTo(task.getId());
                    assertThat(taskResponse.getDeviceOperation()).isEqualTo(task.getDeviceOperation());
                })
                .verifyComplete();

        // Wait for DynamoDB to be updated
        Thread.sleep(1000);

        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 1");
                    assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.OFF);
                })
                .verifyComplete();
    }

    @Test
    @Order(4)
    @DisplayName("Turn Device ON")
    void givenDeviceItem_whenTaskTurnedOn_mustUpdateDeviceStatus() throws InterruptedException {
        task.setDeviceOperation(EnumDeviceOperation.ACTIVATE);
        Mono<Task> deviceUpdateResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(deviceUpdateResponse)
                .assertNext(taskResponse -> {
                    assertThat(taskResponse).isNotNull();
                    assertThat(taskResponse.getId()).isEqualTo(task.getId());
                    assertThat(taskResponse.getDeviceOperation()).isEqualTo(task.getDeviceOperation());
                })
                .verifyComplete();

        // Wait for DynamoDB to be updated
        Thread.sleep(1000);

        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 1");
                    assertThat(device.getDescription()).isEqualTo("Device 1 Description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.ON);
                })
                .verifyComplete();
    }

    @Test
    @Order(5)
    @DisplayName("Update not existing Device ID")
    void givenDeviceItem_whenUpdate_mustThrowException() {
        task.setDeviceId("100");
        Mono<Task> deviceMonoResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(deviceMonoResponse)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify();
    }

    private static Device getNewDevice() {
        return Device.builder()
                .id(DEVICE_ID)
                .name("Device 1")
                .description("Device 1 Description")
                .status(EnumDeviceStatus.ON)
                .build();
    }

    private static void createTable(DynamoDbAsyncClient dbAsyncClient) {
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(Device.TABLE_NAME)
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("id")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        CompletableFuture<CreateTableResponse> createTableFuture = dbAsyncClient.createTable(request);
        createTableFuture.thenCompose(response -> {
            DescribeTableRequest describeRequest = DescribeTableRequest.builder()
                    .tableName(Device.TABLE_NAME)
                    .build();
            return dbAsyncClient.describeTable(describeRequest);

        }).thenCompose(describeResponse -> {
            String tableStatus = describeResponse.table().tableStatusAsString();
            if ("ACTIVE".equals(tableStatus)) {
                CompletableFuture<DescribeTableResponse> completedFuture = new CompletableFuture<>();
                completedFuture.complete(describeResponse);
                return completedFuture;
            }
            DynamoDbAsyncWaiter waiter = dbAsyncClient.waiter();
            DescribeTableRequest waiterRequest = DescribeTableRequest.builder()
                    .tableName(Device.TABLE_NAME)
                    .build();
            return waiter.waitUntilTableExists(waiterRequest)
                    .thenApply(waiterResponse -> describeResponse);
        }).join();
    }
}
