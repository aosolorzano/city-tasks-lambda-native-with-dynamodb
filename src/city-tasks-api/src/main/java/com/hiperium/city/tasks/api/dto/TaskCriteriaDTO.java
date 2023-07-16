package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCriteriaDTO {

    @Min(value = 0, message = "validation.criteria.id.Min.message")
    private Long id;

    private String name;
    private EnumTaskStatus status;
    private String deviceId;
    private EnumDeviceOperation deviceOperation;

    @Min(value = 0, message = "validation.task.hour.Min.message")
    @Max(value = 23, message = "validation.task.hour.Max.message")
    private Integer hour;

    @Min(value = 0, message = "validation.task.minute.Min.message")
    @Max(value = 59, message = "validation.task.minute.Max.message")
    private Integer minute;

    @Length(min = 3, max = 3, message = "validation.task.executionDay.length.message")
    private String executionDay;
}
