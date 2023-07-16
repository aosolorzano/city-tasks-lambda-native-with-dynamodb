package com.hiperium.city.tasks.events.utils;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.DeserializationFeature;
import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.networknt.schema.JsonSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.validation.ValidationException;

import java.io.IOException;
import java.io.InputStream;

import static software.amazon.lambda.powertools.validation.ValidationUtils.getJsonSchema;
import static software.amazon.lambda.powertools.validation.ValidationUtils.validate;

public final class FunctionUtil {

    public static final String ILLEGAL_SCHEMA_MESSAGE = "Error validating JSON schema for Task event: {}";
    public static final String ILLEGAL_STATE_MESSAGE = "JSON schema for Tasks events is not valid. Please check the schema.";
    public static final String VALIDATION_MESSAGE = "Required fields are missing in Task event.";
    public static final String UNMARSHALLING_MESSAGE = "Error unmarshalling the Task event.";

    private static final String VALIDATION_ERROR_MESSAGE = "Some required fields are missing in Task event. Details: {}";
    private static final String UNMARSHALLING_ERROR_MESSAGE = "Error unmarshalling event. Details: {}";

    private static final String CUSTOM_EVENT_SCHEMA_JSON = "classpath:/schemas/custom-event-schema.json";
    private static final ObjectMapper MAPPER = createObjectMapper();
    private static final Logger LOGGER = LogManager.getLogger(FunctionUtil.class);

    private static JsonSchema jsonSchema;


    private FunctionUtil() {
        // Do nothing
    }

    public static void validateAndLoadJsonSchema() {
        try {
            jsonSchema = getJsonSchema(CUSTOM_EVENT_SCHEMA_JSON, true);
        } catch (IllegalArgumentException e) {
            LOGGER.error(ILLEGAL_SCHEMA_MESSAGE, e.getMessage());
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE, e);
        }
    }

    public static void validateEvent(final EventBridgeCustomEvent event) {
        try {
            validate(event, jsonSchema);
        } catch (ValidationException e) {
            LOGGER.error(VALIDATION_ERROR_MESSAGE, e.getMessage());
            throw new IllegalArgumentException(VALIDATION_MESSAGE, e);
        }
    }

    public static <T> T unmarshal(final InputStream inputStream, Class<T> type) {
        try {
            byte[] jsonBytes = inputStream.readAllBytes();
            return MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            LOGGER.error(UNMARSHALLING_ERROR_MESSAGE, e.getMessage());
            throw new IllegalArgumentException(UNMARSHALLING_MESSAGE, e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
