package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import reactor.core.publisher.Flux;

public interface TasksService {

    Flux<TaskDTO> find(TaskCriteriaDTO task);

    Flux<TaskDTO> findAll();
}
