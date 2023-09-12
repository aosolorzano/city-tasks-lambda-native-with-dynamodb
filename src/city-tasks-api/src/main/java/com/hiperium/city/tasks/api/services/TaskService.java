package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.models.Task;
import reactor.core.publisher.Mono;

public interface TaskService {

    Mono<Task> create(Task task);

    Mono<Task> update(Task task);

    Mono<Task> delete(Task task);
}
