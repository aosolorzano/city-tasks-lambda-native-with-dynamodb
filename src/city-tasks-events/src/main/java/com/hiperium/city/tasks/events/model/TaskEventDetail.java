package com.hiperium.city.tasks.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiperium.city.tasks.events.utils.enums.EnumDeviceOperation;
import lombok.Data;

@Data
public class TaskEventDetail {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("deviceOperation")
    private EnumDeviceOperation deviceOperation;
}
