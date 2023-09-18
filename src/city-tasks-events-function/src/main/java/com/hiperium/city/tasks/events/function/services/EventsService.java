package com.hiperium.city.tasks.events.function.services;

import com.hiperium.city.tasks.events.function.mappers.EventMapper;
import com.hiperium.city.tasks.events.function.models.Event;
import com.hiperium.city.tasks.events.function.models.TaskEventDetail;
import com.hiperium.city.tasks.events.function.utils.BeansValidationUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EventsService {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    @Setter(onMethod_ = @Autowired)
    private EventMapper eventMapper;

    public EventsService(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public void createEvent(TaskEventDetail eventDetail) {
        log.debug("createEvent(): {}", eventDetail);
        BeansValidationUtil.validateBean(eventDetail);

        Event event = this.eventMapper.fromTaskEventDetail(eventDetail);
        event.setId(UUID.randomUUID().toString());
        var putItemRequest = PutItemRequest.builder()
                .item(
                        Map.of(Event.ID_FIELD, AttributeValue.fromS(event.getId()),
                                Event.DEVICE_ID_FIELD, AttributeValue.fromS(event.getDeviceId()),
                                Event.TASK_ID_FIELD, AttributeValue.fromN(event.getTaskId().toString()),
                                Event.DEVICE_OPERATION_FIELD, AttributeValue.fromS(event.getDeviceOperation().name()))
                )
                .tableName(Event.TABLE_NAME)
                .build();
        this.dynamoDbAsyncClient.putItem(putItemRequest);
    }
}
