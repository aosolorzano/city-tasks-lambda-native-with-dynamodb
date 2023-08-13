package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.model.Task;

public interface EventBridgeService {

    void triggerEvent(Task task);
}
