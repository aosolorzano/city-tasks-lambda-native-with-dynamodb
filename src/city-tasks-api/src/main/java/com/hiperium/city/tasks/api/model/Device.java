package com.hiperium.city.tasks.api.model;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    public static final String TABLE_NAME = "Devices";

    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String id;
    private String name;
    private String description;
    private EnumDeviceStatus status;
}
