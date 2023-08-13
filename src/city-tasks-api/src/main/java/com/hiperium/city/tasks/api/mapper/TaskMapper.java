package com.hiperium.city.tasks.api.mapper;

import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.dto.TaskResponseDTO;
import com.hiperium.city.tasks.api.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toTask(TaskDTO taskDTO);

    TaskDTO toTaskDto(Task task);

    default TaskResponseDTO toResponseDto(Task task, String zoneId) {
        return TaskResponseDTO.builder()
                .date(ZonedDateTime.now(ZoneId.of(zoneId)))
                .task(this.toTaskDto(task))
                .build();
    }
}
