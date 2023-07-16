package com.hiperium.city.tasks.events;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.utils.FunctionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.logging.Logging;

import java.io.InputStream;
import java.io.OutputStream;

public class ApplicationHandler implements RequestStreamHandler {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationHandler.class);

    static {
        FunctionUtil.validateAndLoadJsonSchema();
    }

    @Logging
    public void handleRequest(final InputStream inputStream, final OutputStream outputStream, final Context context) {
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        LOGGER.debug("handleRequest(): {}", event);
        FunctionUtil.validateEvent(event);
    }
}
