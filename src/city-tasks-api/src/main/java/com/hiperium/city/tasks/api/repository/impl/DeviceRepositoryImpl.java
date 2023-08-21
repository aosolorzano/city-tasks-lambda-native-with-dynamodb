package com.hiperium.city.tasks.api.repository.impl;

import com.hiperium.city.tasks.api.exception.ApplicationException;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DynamoDbAsyncTable<Device> dynamoDbAsyncTable;

    public DeviceRepositoryImpl(DynamoDbAsyncTable<Device> dynamoDbAsyncTable) {
        this.dynamoDbAsyncTable = dynamoDbAsyncTable;
    }

    public Mono<Task> updateStatusByTaskOperation(final Task task) {
        return this.findById(task.getDeviceId())
                .doOnNext(device -> setStatusByTaskOperation(task, device))
                .doOnNext(this::updateDeviceItem)
                .thenReturn(task);
    }

    public Mono<Device> findById(final String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(() -> this.dynamoDbAsyncTable.getItem(key))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(EnumResourceError.DEVICE_NOT_FOUND, id)));
    }

    private void updateDeviceItem(final Device device) {
        Mono.fromFuture(this.dynamoDbAsyncTable.putItem(device))
                .doOnError(DeviceRepositoryImpl::throwCustomError)
                .subscribe();
    }

    private static void setStatusByTaskOperation(final Task task, final Device device) {
        if (EnumDeviceOperation.ACTIVATE.equals(task.getDeviceOperation())) {
            device.setStatus(EnumDeviceStatus.ON);
        } else {
            device.setStatus(EnumDeviceStatus.OFF);
        }
    }

    private static void throwCustomError(Throwable error) {
        throw new ApplicationException(error.getMessage(), error);
    }
}
