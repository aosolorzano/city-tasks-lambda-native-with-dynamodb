package com.hiperium.city.tasks.api.mappers;

import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.dto.TaskResponseDTO;
import com.hiperium.city.tasks.api.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "jobId", ignore = true)
    @Mapping(target = "deviceExecutionCommand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toTask(TaskDTO taskDTO);

    TaskDTO toTaskDto(Task task);

    default TaskResponseDTO toResponseDto(Task task, String zoneId) {
        return TaskResponseDTO.builder()
                .date(ZonedDateTime.now(ZoneId.of(zoneId)))
                .task(this.toTaskDto(task))
                .build();
    }
}
