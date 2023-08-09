package com.hiperium.city.tasks.events;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.utils.FunctionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class ApplicationHandler implements RequestStreamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHandler.class);

    static {
        // FunctionUtil.validateAndLoadJsonSchema();
    }

    public void handleRequest(final InputStream inputStream, final OutputStream outputStream, final Context context) {
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        LOGGER.debug("handleRequest(): {}", event);
    }
}
