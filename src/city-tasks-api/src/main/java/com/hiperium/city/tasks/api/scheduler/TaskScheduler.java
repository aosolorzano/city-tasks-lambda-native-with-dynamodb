package com.hiperium.city.tasks.api.scheduler;

import com.hiperium.city.tasks.api.model.Task;

public interface TaskScheduler {

    void scheduleJob(Task task);

    void rescheduleJob(Task task);

    void unscheduleJob(Task task);
}
