package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumTaskOperation;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Tasks details for the create, update, and delete operations.", example = "CREATE")
    @NotNull(message = "validation.task.operation.enum.NotNull.message")
    private EnumTaskOperation operation;

    @Schema(description = "Tasks details for the create, update, and delete operations.")
    @NotNull(message = "validation.task.operation.dto.NotNull.message")
    private TaskDTO task;
}
