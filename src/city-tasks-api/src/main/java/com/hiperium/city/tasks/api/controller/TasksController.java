package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.service.TasksService;
import com.hiperium.city.tasks.api.utils.BeanValidationUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping(TaskUtil.TASKS_PATH)
public class TasksController {

    private final TasksService tasksService;

    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TaskDTO> findAll() {
        log.debug("findAll() - START");
        return this.tasksService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TaskDTO> find(@RequestBody TaskCriteriaDTO criteriaDto) {
        log.debug("find() - START: {}", criteriaDto);
        BeanValidationUtil.validateBean(criteriaDto);
        return this.tasksService.find(criteriaDto);
    }

    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public TaskCriteriaDTO getTemplateBody() {
        return TaskUtil.getTaskCriteriaTemplate();
    }
}
