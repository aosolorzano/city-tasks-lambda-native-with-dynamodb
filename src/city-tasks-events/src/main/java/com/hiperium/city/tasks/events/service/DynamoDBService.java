package com.hiperium.city.tasks.events.service;

import com.hiperium.city.tasks.events.mapper.EventMapper;
import com.hiperium.city.tasks.events.model.Event;
import com.hiperium.city.tasks.events.model.TaskEventDetail;
import com.hiperium.city.tasks.events.utils.DynamoDBUtil;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

@Slf4j
public class DynamoDBService {

    private final DynamoDbAsyncTable<Event> eventTable;

    public DynamoDBService() {
        var dynamoDbAsyncClient = DynamoDBUtil.getDynamoDbClient();
        var dynamoDbEnhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
        this.eventTable = dynamoDbEnhancedAsyncClient.table(Event.TABLE_NAME, TableSchema.fromBean(Event.class));
    }

    public void createEventItem(TaskEventDetail eventDetail) {
        Event event = Mappers.getMapper(EventMapper.class).fromTaskEventDetail(eventDetail);
        event.setId(UUID.randomUUID().toString());
        log.debug("createEventItem(): {}", event);
        this.eventTable.putItem(event);
    }
}
