package com.hiperium.city.tasks.events;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.service.DynamoDBService;
import com.hiperium.city.tasks.events.utils.FunctionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class ApplicationHandler implements RequestStreamHandler {

    private final DynamoDBService dynamoDBService;

    public ApplicationHandler() {
        log.debug("ApplicationHandler() - START");
        FunctionUtil.validateAndLoadJsonSchema();
        this.dynamoDBService = new DynamoDBService();
    }

    public void handleRequest(final InputStream inputStream, final OutputStream outputStream, final Context context) {
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        log.debug("handleRequest(): {}", event);
        FunctionUtil.validateEvent(event);
        this.dynamoDBService.createEventItem(event.getDetail());
    }
}
