package com.hiperium.city.tasks.events.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public final class FunctionUtil {

    public static final String ILLEGAL_SCHEMA_MESSAGE = "Error validating JSON schema for Task event: {}";
    public static final String VALIDATION_MESSAGE = "Required fields are missing in Task events.";
    public static final String UNMARSHALLING_MESSAGE = "Error unmarshalling the Task events.";

    private static final String CUSTOM_EVENT_SCHEMA_JSON = "classpath:/schemas/custom-event-schema.json";


    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionUtil.class);
    private static final ObjectMapper MAPPER = createObjectMapper();

    // private static JsonSchema jsonSchema;

    private FunctionUtil() {
        // Do nothing
    }

//    public static void validateAndLoadJsonSchema() {
//        try {
//            jsonSchema = getJsonSchema(CUSTOM_EVENT_SCHEMA_JSON, true);
//        } catch (IllegalArgumentException e) {
//            LOGGER.error(ILLEGAL_SCHEMA_MESSAGE, e.getMessage());
//        }
//    }

//    public static void validateEvent(final EventBridgeCustomEvent event) {
//        try {
//            validate(event, jsonSchema);
//        } catch (ValidationException e) {
//            throw new IllegalArgumentException(VALIDATION_MESSAGE, e);
//        }
//    }

    public static <T> T unmarshal(final InputStream inputStream, Class<T> type) {
        try {
            byte[] jsonBytes = inputStream.readAllBytes();
            return MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(UNMARSHALLING_MESSAGE, e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
