package com.hiperium.city.tasks.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.dto.TaskExecutionDTO;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.EventBridgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

import java.util.Objects;

@Slf4j
@Service
public class EventBridgeServiceImpl implements EventBridgeService {

    public static final String EVENT_SOURCE_NAME = "com.hiperium.city.tasks";
    public static final String EVENT_SOURCE_TYPE = "TaskExecution";
    private final EventBridgeClient eventBridgeClient;

    public EventBridgeServiceImpl(EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
    }

    @Override
    public void triggerEvent(final Task task) {
        log.debug("triggerCustomEvent() - START: {}", task);
        ObjectMapper objectMapper = new ObjectMapper();
        TaskExecutionDTO taskExecutionDTO = TaskExecutionDTO.builder()
                .taskId(task.getId())
                .deviceId(task.getDeviceId())
                .deviceOperation(task.getDeviceOperation())
                .executionDateTime(System.currentTimeMillis())
                .build();
        PutEventsRequestEntry entry = createRequestEntry(objectMapper, taskExecutionDTO);
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

    private static PutEventsRequestEntry createRequestEntry(ObjectMapper objectMapper, TaskExecutionDTO taskExecutionDto) {
        PutEventsRequestEntry entry;
        try {
            entry = PutEventsRequestEntry.builder()
                    .source(EVENT_SOURCE_NAME)
                    .detailType(EVENT_SOURCE_TYPE)
                    .detail(objectMapper.writeValueAsString(taskExecutionDto))
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return entry;
    }
}
