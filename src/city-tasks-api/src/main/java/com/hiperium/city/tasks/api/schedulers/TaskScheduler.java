package com.hiperium.city.tasks.api.schedulers;

import com.hiperium.city.tasks.api.models.Task;

public interface TaskScheduler {

    void scheduleJob(Task task);

    void rescheduleJob(Task task);

    void unscheduleJob(Task task);
}
