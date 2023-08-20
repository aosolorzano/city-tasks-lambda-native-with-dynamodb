package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.events.model.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.utils.DynamoDBUtil;
import com.hiperium.city.tasks.events.utils.FunctionUtil;
import com.hiperium.city.tasks.events.utils.TestsUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationHandlerTest extends AbstractContainerBaseTest {

    @BeforeAll
    static void beforeAll() {
        DynamoDbClient dynamoDbClient = DynamoDBUtil.getDynamoDbClient();
        TestsUtil.verifyIfTableIsCreated(dynamoDbClient);
    }

    @Test
    @Order(1)
    void givenJsonSchema_whenValidateIt_thenNoExceptionMustThrown() {
        assertDoesNotThrow(FunctionUtil::validateAndLoadJsonSchema);
    }

    @Test
    @Order(2)
    void givenValidInputEvent_whenUnmarshal_thenResultMustNotBeNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/lambda-valid-event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
    }

    @Test
    @Order(3)
    void givenValidInputEvent_whenValidateObject_thenNoExceptionMustThrown() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/lambda-valid-event.json");
        assertNotNull(inputStream);
        EventBridgeCustomEvent event = FunctionUtil.unmarshal(inputStream, EventBridgeCustomEvent.class);
        assertNotNull(event);
        FunctionUtil.validateEvent(event);
    }

    @Test
    @Order(4)
    void givenValidEvent_whenInvokeLambdaFunction_thenExecuteSuccessfully() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/lambda-valid-event.json")) {
            ApplicationHandler handler = new ApplicationHandler();
            assertDoesNotThrow(() -> handler.handleRequest(inputStream, null, null));
        }
    }

    @Order(5)
    @ParameterizedTest
    @ValueSource(strings = {
            "events/lambda-event-invalid-detail.json",
            "events/lambda-event-without-detail.json"})
    void givenEventList_whenInvokeLambdaFunction_thenReturnOkResponse(String jsonFilePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
            ApplicationHandler handler = new ApplicationHandler();
            assertThrows(IllegalArgumentException.class, () ->
                    handler.handleRequest(inputStream, null, null));
        }
    }
}
