package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.models.Task;

public interface EventBridgeService {

    void triggerEvent(Task task);
}
