package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.dto.TaskOperationDTO;
import com.hiperium.city.tasks.api.dto.TaskResponseDTO;
import com.hiperium.city.tasks.api.mapper.TaskMapper;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.BeanValidationUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.hiperium.city.tasks.api.utils.PathsUtil.*;

@Slf4j
@RestController
@RequestMapping(CONTEXT_PATH + API_VERSION_1 + TASK_PATH)
@Tag(name = "Task", description = "Task management API.")
public class TaskController {

    private final TaskService taskService;

    @Setter(onMethod = @__({@Autowired}))
    private TaskMapper taskMapper;

    @Value("${city.tasks.time.zone}")
    private String zoneId;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Manage single Task operation like create, update and delete.",
               description = "Single endpoint to manage create, update and delete operations over single Task object.")
    public Mono<TaskResponseDTO> taskOperation(@RequestBody @Valid TaskOperationDTO operationDto) {
        log.debug("taskOperation() - START: {}", operationDto);
        BeanValidationUtil.validateBean(operationDto.getTask());
        Task task = this.taskMapper.toTask(operationDto.getTask());
        return (switch (operationDto.getOperation()) {
            case CREATE -> this.taskService.create(task);
            case UPDATE -> this.taskService.update(task);
            case DELETE -> this.taskService.delete(task);
        }).map(taskResponse -> this.taskMapper.toResponseDto(taskResponse, this.zoneId));
    }

    @Hidden
    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public TaskOperationDTO getTaskOperationTemplate() {
        return TaskUtil.getTaskOperationTemplate();
    }
}
