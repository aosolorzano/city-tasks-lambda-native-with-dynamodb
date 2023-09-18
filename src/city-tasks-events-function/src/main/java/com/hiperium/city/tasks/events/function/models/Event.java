package com.hiperium.city.tasks.events.function.models;

import com.hiperium.city.tasks.events.function.utils.enums.EnumDeviceOperation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Event {

    public static final String TABLE_NAME = "Events";
    public static final String ID_FIELD = "id";
    public static final String DEVICE_ID_FIELD = "deviceId";
    public static final String TASK_ID_FIELD = "taskId";
    public static final String DEVICE_OPERATION_FIELD = "deviceOperation";

    private String id;
    private String deviceId;
    private Long taskId;
    private EnumDeviceOperation deviceOperation;
}
