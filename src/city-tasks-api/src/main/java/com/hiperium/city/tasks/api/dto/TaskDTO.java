package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    @NotEmpty(message = "validation.task.name.NotEmpty.message")
    private String name;

    private String description;

    private EnumTaskStatus status;

    @NotEmpty(message = "validation.device.id.NotEmpty.message")
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "validation.device.action.NotEmpty.message")
    private EnumDeviceOperation deviceOperation;

    @Min(value = 0, message = "validation.task.hour.Min.message")
    @Max(value = 23, message = "validation.task.hour.Max.message")
    private int hour;

    @Min(value = 0, message = "validation.task.minute.Min.message")
    @Max(value = 59, message = "validation.task.minute.Max.message")
    private int minute;

    @NotEmpty(message = "validation.task.execution.days.NotEmpty.message")
    private String executionDays;

    @Future(message = "validation.task.execute.until.Future.message")
    private ZonedDateTime executeUntil;
}
