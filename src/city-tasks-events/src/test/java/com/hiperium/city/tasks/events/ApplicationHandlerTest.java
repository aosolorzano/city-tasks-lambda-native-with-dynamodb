package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.utils.FunctionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class ApplicationHandlerTest {

    @Test
    @Order(1)
    void givenJsonSchema_whenValidateIt_thenNoExceptionMustThrown() {
        assertDoesNotThrow(FunctionUtil::validateAndLoadJsonSchema);
    }

    @Test
    @Order(2)
    void givenValidInputEvent_whenUnmarshal_thenResultMustNotBeNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
    }

    @Test
    @Order(3)
    void givenValidInputEvent_whenValidateObject_thenNoExceptionMustThrown() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
        FunctionUtil.validateEvent(event);
    }

    @Test
    @Order(4)
    void givenValidEvent_whenInvokeLambdaFunction_thenExecuteSuccessfully() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/event.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            assertDoesNotThrow(() -> handler.handleRequest(inputStream, null, null));
        }
    }

    @Test
    @Order(5)
    void givenEventWithInvalidDetail_whenInvokeLambdaFunction_thenThrowError() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/invalid-event-detail.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                    () -> handler.handleRequest(inputStream, null, null), "IllegalArgumentException expected.");
            assertEquals(FunctionUtil.UNMARSHALLING_INPUT_ERROR, expectedException.getMessage());
        }
    }

    @Test
    @Order(6)
    void givenEventWithoutDetail_whenInvokeLambdaFunction_thenThrowError() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/event-without-detail.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                    () -> handler.handleRequest(inputStream, null, null), "IllegalArgumentException expected.");
            assertEquals(FunctionUtil.EVENT_OBJECT_VALIDATION_ERROR, expectedException.getMessage());
        }
    }
}

