package com.hiperium.city.tasks.events.service;

import com.hiperium.city.tasks.events.mapper.EventMapper;
import com.hiperium.city.tasks.events.model.Event;
import com.hiperium.city.tasks.events.model.TaskEventDetail;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.UUID;

@Slf4j
public class DynamoDBService {

    private final DynamoDbTable<Event> eventTable;

    public DynamoDBService(final DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.eventTable = dynamoDbEnhancedClient.table(Event.TABLE_NAME, TableSchema.fromBean(Event.class));
    }

    public void createEventItem(TaskEventDetail eventDetail) {
        Event event = Mappers.getMapper(EventMapper.class).fromTaskEventDetail(eventDetail);
        event.setId(UUID.randomUUID().toString());
        log.debug("createEventItem(): {}", event);
        this.eventTable.putItem(event);
    }
}
