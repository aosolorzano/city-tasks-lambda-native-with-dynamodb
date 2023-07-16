package com.hiperium.city.tasks.api.scheduler.impl;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.exception.TaskSchedulerException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.scheduler.TaskScheduler;
import com.hiperium.city.tasks.api.utils.SchedulerUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import com.hiperium.city.tasks.api.utils.enums.EnumSchedulerError;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class TaskSchedulerImpl implements TaskScheduler {

    private final Scheduler scheduler;

    @Value("${city.tasks.time.zone}")
    private String timeZone;

    public TaskSchedulerImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleJob(final Task task) {
        log.debug("scheduleJob() - START: {}", task);
        JobDetail job = SchedulerUtil.createJobDetailFromTask(task);
        Trigger trigger = SchedulerUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.SCHEDULE_JOB_ERROR, task.getName());
        }
    }

    public void rescheduleJob(final Task task) {
        log.debug("rescheduleJob() - START: {}", task);
        Trigger currentTrigger = this.getCurrentTrigger(task);
        Trigger newTrigger = SchedulerUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.rescheduleJob(currentTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.RESCHEDULE_JOB_ERROR, task.getId());
        }
    }

    public void unscheduleJob(final Task task) {
        log.debug("unscheduleJob() - START: {}", task);
        Trigger currentTrigger = this.getCurrentTrigger(task);
        try {
            this.scheduler.unscheduleJob(currentTrigger.getKey());
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.UNSCHEDULE_JOB_ERROR, task.getId());
        }
    }

    private Trigger getCurrentTrigger(final Task task) {
        Trigger trigger = null;
        try {
            for (JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SchedulerUtil.TASK_GROUP_NAME))) {
                if (jobKey.getName().equals(task.getJobId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), SchedulerUtil.TASK_GROUP_NAME);
                    trigger = this.scheduler.getTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.GET_CURRENT_TRIGGER_ERROR, task.getId());
        }
        if (Objects.isNull(trigger)) {
            throw new ResourceNotFoundException(EnumResourceError.TRIGGER_NOT_FOUND, task.getId());
        }
        return trigger;
    }
}
