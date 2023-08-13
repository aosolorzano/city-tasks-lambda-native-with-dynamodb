package com.hiperium.city.tasks.events.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.networknt.schema.JsonSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.lambda.powertools.validation.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import static software.amazon.lambda.powertools.validation.ValidationUtils.getJsonSchema;
import static software.amazon.lambda.powertools.validation.ValidationUtils.validate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FunctionUtil {

    private static final String EVENT_OBJECT_VALIDATION_ERROR = "Required fields are missing in Task events.";
    private static final String UNMARSHALLING_INPUT_ERROR = "Error unmarshalling the Task events.";

    private static final String INVALID_JSON_SCHEMA_DEFINITION_ERROR = "JSON Schema definition is not valid.";
    private static final String JSON_SCHEMA_FILE_NOT_FOUND_ERROR = "JSON Schema file not found.";
    private static final String JSON_SCHEMA_FILE_LOCATION = "/schemas/custom-event-schema.json";


    private static final ObjectMapper MAPPER = createObjectMapper();

    private static JsonSchema jsonSchema;

    public static void validateAndLoadJsonSchema() {
        try {
            String jsonSchemaString = readJsonSchemaFile();
            jsonSchema = getJsonSchema(jsonSchemaString, true);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_JSON_SCHEMA_DEFINITION_ERROR, e);
        }
    }

    public static void validateEvent(final EventBridgeCustomEvent event) {
        try {
            validate(event, jsonSchema);
        } catch (ValidationException e) {
            throw new IllegalArgumentException(EVENT_OBJECT_VALIDATION_ERROR, e);
        }
    }

    public static <T> T unmarshal(final InputStream inputStream, Class<T> type) {
        try {
            byte[] jsonBytes = inputStream.readAllBytes();
            return MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(UNMARSHALLING_INPUT_ERROR, e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static String readJsonSchemaFile() {
        InputStream inputStream = FunctionUtil.class.getResourceAsStream(JSON_SCHEMA_FILE_LOCATION);
        if (Objects.isNull(inputStream)) {
            throw new IllegalArgumentException(JSON_SCHEMA_FILE_NOT_FOUND_ERROR);
        }
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}
