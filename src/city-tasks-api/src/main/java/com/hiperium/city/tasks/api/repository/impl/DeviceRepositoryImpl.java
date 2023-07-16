package com.hiperium.city.tasks.api.repository.impl;

import com.hiperium.city.tasks.api.exception.ApplicationException;
import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.utils.DeviceUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public DeviceRepositoryImpl(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Task> updateStatusByTaskOperation(final Task task) {
        return this.findById(task.getDeviceId())
                .doOnNext(device -> setStatusByTaskOperation(task, device))
                .map(DeviceUtil::createPutRequest)
                .doOnNext(this::updateDeviceItem)
                .thenReturn(task);
    }

    public Mono<Device> findById(final String id) {
        GetItemRequest getItemRequest = DeviceUtil.createGetRequest(id);
        return Mono.fromFuture(this.dynamoDbAsyncClient.getItem(getItemRequest))
                .doOnNext(itemResponse -> verifyResponse(id, itemResponse))
                .map(DeviceUtil::getFromItemResponse);
    }

    private void updateDeviceItem(PutItemRequest putItemRequest) {
        Mono.fromFuture(this.dynamoDbAsyncClient.putItem(putItemRequest))
                .doOnError(DeviceRepositoryImpl::createCustomError)
                .subscribe();
    }

    private static void createCustomError(Throwable error) {
        throw new ApplicationException(error.getMessage(), error);
    }

    private static void setStatusByTaskOperation(final Task task, final Device device) {
        if (EnumDeviceOperation.ACTIVATE.equals(task.getDeviceOperation())) {
            device.setStatus(EnumDeviceStatus.ON);
        } else {
            device.setStatus(EnumDeviceStatus.OFF);
        }
    }

    private static void verifyResponse(String id, GetItemResponse itemResponse) {
        if(!itemResponse.hasItem()) throw new ResourceNotFoundException(EnumResourceError.DEVICE_NOT_FOUND, id);
    }
}
