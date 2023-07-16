package com.hiperium.city.tasks.events.model;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.annotation.JsonProperty;
import com.hiperium.city.tasks.events.utils.enums.EnumDeviceOperation;

public class TaskEventDetail {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("deviceOperation")
    private EnumDeviceOperation deviceOperation;

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

    @Override
    public String toString() {
        return "TaskEventDetail{" +
                "taskId=" + taskId +
                ", deviceId='" + deviceId + '\'' +
                ", deviceOperation=" + deviceOperation +
                '}';
    }
}
