package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import com.hiperium.city.tasks.api.utils.TestsUtil;
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

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceRepositoryTest extends AbstractContainerBaseTest {

    public static final String DEVICE_ID = "123";
    public static final Long WAIT_TIME_BETWEEN_CALLS = 500L;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    private static Task task;

    @BeforeAll
    public static void beforeAll() {
        task = TaskUtil.getTaskTemplate();
        task.setDeviceId(DEVICE_ID);
    }

    @Test
    @Order(1)
    @DisplayName("Wait for DynamoDB to be ready")
    void givenDevicesTable_whenCreated_mustNotThrownError() {
        TestsUtil.verifyIfTableIsCreated(this.dynamoDbAsyncClient);
    }

    @Test
    @Order(2)
    @DisplayName("Find Device by ID")
    void givenDeviceId_whenFindById_mustReturnDeviceItem() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceMonoResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 123");
                    assertThat(device.getDescription()).isEqualTo("Device 123 description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.OFF);
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Find not existing Device ID")
    void givenDeviceId_whenFindById_mustThrowException() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById("1000");
        StepVerifier.create(deviceMonoResponse)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @Order(4)
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

        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 123");
                    assertThat(device.getDescription()).isEqualTo("Device 123 description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.OFF);
                })
                .verifyComplete();
    }

    @Test
    @Order(5)
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
        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID)
                .delaySubscription(Duration.ofMillis(WAIT_TIME_BETWEEN_CALLS))
                .single();

        //Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 123");
                    assertThat(device.getDescription()).isEqualTo("Device 123 description");
                    assertThat(device.getStatus()).isEqualTo(EnumDeviceStatus.ON);
                })
                .verifyComplete();
    }

    @Test
    @Order(6)
    @DisplayName("Update not existing Device ID")
    void givenDeviceItem_whenUpdate_mustThrowException() {
        task.setDeviceId("100");
        Mono<Task> deviceMonoResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(deviceMonoResponse)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify();
    }
}
