package com.hiperium.city.tasks.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiperium.city.tasks.events.utils.enums.EnumDeviceOperation;

//@Data
public class TaskEventDetail {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("deviceOperation")
    private EnumDeviceOperation deviceOperation;

    @JsonProperty("executionDateTime")
    private Long executionDateTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public EnumDeviceOperation getDeviceOperation() {
        return deviceOperation;
    }

    public void setDeviceOperation(EnumDeviceOperation deviceOperation) {
        this.deviceOperation = deviceOperation;
    }

    public Long getExecutionDateTime() {
        return executionDateTime;
    }

    public void setExecutionDateTime(Long executionDateTime) {
        this.executionDateTime = executionDateTime;
    }
}
