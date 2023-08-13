package com.hiperium.city.tasks.api.service.impl;

import com.hiperium.city.tasks.api.dao.TasksDAO;
import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.mapper.TaskMapper;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.service.TasksService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TasksServiceImpl implements TasksService {

    private final TaskRepository taskRepository;
    private final TasksDAO tasksDao;

    @Setter(onMethod = @__({@Autowired}))
    private TaskMapper taskMapper;

    public TasksServiceImpl(TaskRepository taskRepository, TasksDAO tasksDao) {
        this.taskRepository = taskRepository;
        this.tasksDao = tasksDao;
    }

    public Flux<TaskDTO> findAll() {
        return Flux.fromStream(() -> this.taskRepository.findAll().stream())
                .map(this.taskMapper::toTaskDto);
    }

    public Flux<TaskDTO> find(final TaskCriteriaDTO criteriaDto) {
        return Flux.fromStream(() -> this.tasksDao.find(criteriaDto).stream());
    }
}
