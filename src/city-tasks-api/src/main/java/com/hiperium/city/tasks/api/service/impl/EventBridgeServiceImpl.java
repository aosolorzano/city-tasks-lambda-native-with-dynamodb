package com.hiperium.city.tasks.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.dto.TaskExecutionDTO;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.EventBridgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EventBridgeServiceImpl implements EventBridgeService {

    public static final String EVENT_SOURCE_NAME = "com.hiperium.city.tasks";
    public static final String EVENT_SOURCE_TYPE = "TaskExecution";
    private final EventBridgeAsyncClient eventBridgeAsyncClient;

    @Value("${city.tasks.time.zone}")
    private static String timeZone;

    public EventBridgeServiceImpl(EventBridgeAsyncClient eventBridgeAsyncClient) {
        this.eventBridgeAsyncClient = eventBridgeAsyncClient;
    }

    @Override
    public void triggerEvent(final Task task) {
        log.debug("triggerCustomEvent() - START");
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
        log.debug("Event Request: {}", eventRequest);

        CompletableFuture<PutEventsResponse> response = this.eventBridgeAsyncClient.putEvents(eventRequest);
        response.thenAccept(resp -> {
            for (PutEventsResultEntry resultEntry : resp.entries()) {
                if (Objects.nonNull(resultEntry.eventId())) {
                    log.info("Event ID: {} sent successfully to EventBridge.", resultEntry.eventId());
                } else {
                    log.error("Error sending events to EventBridge. Error Code: {}. Error message: {}.",
                            resultEntry.errorCode(), resultEntry.errorMessage());
                }
            }
        }).join();
    }

    private static PutEventsRequestEntry createRequestEntry(ObjectMapper objectMapper, TaskExecutionDTO taskExecutionDto) {
        PutEventsRequestEntry entry;
        ZonedDateTime actualZonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        try {
            entry = PutEventsRequestEntry.builder()
                    .source(EVENT_SOURCE_NAME)
                    .detailType(EVENT_SOURCE_TYPE)
                    .detail(objectMapper.writeValueAsString(taskExecutionDto))
                    .time(actualZonedDateTime.toInstant())
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return entry;
    }
}
