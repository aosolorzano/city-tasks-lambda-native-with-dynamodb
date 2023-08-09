package com.hiperium.city.tasks.api.scheduler.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.dto.TaskEventDTO;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.SchedulerUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

import java.util.Objects;

@Slf4j
@Component
public class JobExecution implements Job {

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;
    private final EventBridgeClient eventBridgeClient;

    public JobExecution(TaskRepository taskRepository, DeviceRepository deviceRepository,
                        EventBridgeClient eventBridgeClient) {
        this.taskRepository = taskRepository;
        this.deviceRepository = deviceRepository;
        this.eventBridgeClient = eventBridgeClient;
    }

    @Override
    public void execute(final JobExecutionContext context) {
        log.debug("execute() - START: {}", context);
        final String jobId = context.getJobDetail().getJobDataMap().getString(SchedulerUtil.TASK_JOB_ID_DATA_KEY);
        Mono.just(jobId)
                .map(this.taskRepository::findByJobId)
                .flatMap(this.deviceRepository::updateStatusByTaskOperation)
                .doOnNext(this::triggerCustomEvent)
                .subscribe(
                        result -> log.info("Job executed: {}", jobId),
                        error -> log.error("Error executing job {}. Error: {}", jobId, error.getMessage())
                );
    }

    private void triggerCustomEvent(final Task task) {
        log.debug("triggerCustomEvent() - START: {}", task);
        ObjectMapper objectMapper = new ObjectMapper();
        TaskEventDTO taskEventDto = TaskEventDTO.builder()
                .taskId(task.getId())
                .deviceId(task.getDeviceId())
                .deviceOperation(task.getDeviceOperation())
                .build();
        PutEventsRequestEntry entry = createRequestEntry(objectMapper, taskEventDto);
        PutEventsRequest eventRequest = PutEventsRequest.builder()
                .entries(entry)
                .build();

        try {
            PutEventsResponse result = this.eventBridgeClient.putEvents(eventRequest);
            for (PutEventsResultEntry resultEntry : result.entries()) {
                if (Objects.nonNull(resultEntry.eventId())) {
                    log.info("Event ID: {} sent successfully to EventBridge.", resultEntry.eventId());
                } else {
                    log.error("Error sending events to EventBridge. Error Code: {}. Error message: {}.",
                            resultEntry.errorCode(), resultEntry.errorMessage());
                }
            }
        } catch (EventBridgeException e) {
            log.error("Error sending events to EventBridge. Error Code: {}. Error message: {}.",
                    e.awsErrorDetails().errorCode(), e.awsErrorDetails().errorMessage());
        }
    }

    private static PutEventsRequestEntry createRequestEntry(ObjectMapper objectMapper, TaskEventDTO taskEventDto) {
        PutEventsRequestEntry entry;
        try {
            entry = PutEventsRequestEntry.builder()
                    .source("com.hiperium.city.tasks")
                    .detailType("TaskExecution")
                    .detail(objectMapper.writeValueAsString(taskEventDto))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error serializing Task Event: {}.", e.getMessage());
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return entry;
    }
}

