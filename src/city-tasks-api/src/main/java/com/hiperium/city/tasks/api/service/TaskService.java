package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.model.Task;
import reactor.core.publisher.Mono;

public interface TaskService {

    Mono<Task> create(Task task);

    Mono<Task> update(Task task);

    Mono<Task> delete(Task task);
}
