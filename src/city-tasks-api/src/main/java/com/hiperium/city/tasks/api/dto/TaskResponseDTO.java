package com.hiperium.city.tasks.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    @Schema(description = "Date and time of the search result.", example = "2021-08-01T00:00:00.000Z")
    private ZonedDateTime date;

    @Schema(description = "Task details as a search result.")
    private TaskDTO task;
}
