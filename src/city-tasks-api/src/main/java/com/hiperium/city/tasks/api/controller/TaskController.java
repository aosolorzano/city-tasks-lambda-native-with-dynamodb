package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.dto.TaskOperationDTO;
import com.hiperium.city.tasks.api.dto.TaskResponseDTO;
import com.hiperium.city.tasks.api.mapper.TaskMapper;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.BeanValidationUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(TaskUtil.TASK_PATH)
public class TaskController {

    private final TaskService taskService;

    @Value("${city.tasks.time.zone}")
    private String zoneId;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<TaskResponseDTO> taskOperation(@RequestBody @Valid TaskOperationDTO operationDto) {
        log.debug("taskOperation() - START: {}", operationDto);
        BeanValidationUtil.validateBean(operationDto.getTask());
        Task task = TaskMapper.INSTANCE.toTask(operationDto.getTask());
        return (switch (operationDto.getOperation()) {
            case CREATE -> this.taskService.create(task);
            case UPDATE -> this.taskService.update(task);
            case DELETE -> this.taskService.delete(task);
        }).map(taskResponse -> TaskMapper.INSTANCE.toResponseDto(taskResponse, this.zoneId));
    }

    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public TaskOperationDTO getTaskOperationTemplate() {
        return TaskUtil.getTaskOperationTemplate();
    }
}
