package com.hiperium.city.tasks.api.dao;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;

import java.util.List;

public interface TasksDAO {

    List<TaskDTO> find(TaskCriteriaDTO customerCriteriaDTO);
}
