package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.common.AbstractContainerBaseTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationHandlerITest extends AbstractContainerBaseTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "events/valid-event.json",
            "events/invalid-event-detail.json",
            "events/event-without-detail.json"})
    void givenEventList_whenInvokeLambdaFunction_thenReturn200(String jsonFilePath) {
        await().atMost(30, TimeUnit.SECONDS).until(this.verifyLambdaActiveState());
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(FUNCTION_NAME)
                .payload(getPayloadBytes(jsonFilePath))
                .build();
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
        assertEquals(200, invokeResponse.statusCode());
    }

    private static SdkBytes getPayloadBytes(final String jsonFilePath) {
        ClassLoader classLoader = ApplicationHandlerITest.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(jsonFilePath)) {
            assert inputStream != null;
            byte[] jsonBytes = IOUtils.toByteArray(inputStream);
            return SdkBytes.fromByteArray(jsonBytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private Callable<Boolean> verifyLambdaActiveState() {
        return () -> {
            GetFunctionConfigurationRequest getRequest = GetFunctionConfigurationRequest.builder()
                    .functionName(FUNCTION_NAME)
                    .build();
            GetFunctionConfigurationResponse getResponse = lambdaClient.getFunctionConfiguration(getRequest);
            return getResponse.stateAsString().equals(State.ACTIVE.toString());
        };
    }
}

