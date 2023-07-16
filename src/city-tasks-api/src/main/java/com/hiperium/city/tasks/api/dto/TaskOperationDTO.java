package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumTaskOperation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskOperationDTO {

    @NotNull(message = "validation.task.operation.enum.NotNull.message")
    private EnumTaskOperation operation;

    @NotNull(message = "validation.task.operation.dto.NotNull.message")
    private TaskDTO task;
}
