package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.ApplicationHandler;
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

    @BeforeAll
    static void init() {
        FunctionUtil.validateAndLoadJsonSchema();
    }

    @Test
    @Order(1)
    void mustUnmarshallEventFromInputStreamTest() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/valid-event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
    }

    @Test
    @Order(2)
    void mustValidateEventObjectTest() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/valid-event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
        FunctionUtil.validateEvent(event);
    }

    @Test
    @Order(3)
    void givenValidEvent_whenInvokeLambdaFunction_thenExecuteSuccessfully() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/valid-event.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            assertDoesNotThrow(() -> handler.handleRequest(inputStream, null, null));
        }
    }

    @Test
    @Order(4)
    void givenEventWithInvalidDetail_whenInvokeLambdaFunction_thenThrowError() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/invalid-event-detail.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                    () -> handler.handleRequest(inputStream, null, null), "IllegalArgumentException expected.");
            assertEquals(FunctionUtil.UNMARSHALLING_MESSAGE, expectedException.getMessage());
        }
    }

    @Test
    @Order(5)
    void givenEventWithoutDetail_whenInvokeLambdaFunction_thenThrowError() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/event-without-detail.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                    () -> handler.handleRequest(inputStream, null, null), "IllegalArgumentException expected.");
            assertEquals(FunctionUtil.VALIDATION_MESSAGE, expectedException.getMessage());
        }
    }
}

