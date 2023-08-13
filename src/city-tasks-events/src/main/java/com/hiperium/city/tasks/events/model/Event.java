package com.hiperium.city.tasks.events.model;

import com.hiperium.city.tasks.events.utils.enums.EnumDeviceOperation;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class Event {

    public static final String TABLE_NAME = "Events";

    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String id;

    @Getter(onMethod_ = @DynamoDbSortKey)
    private String deviceId;

    private Long taskId;

    private EnumDeviceOperation deviceOperation;

    private Long executionDateTime;
}
