package com.hiperium.city.tasks.events.function;

import com.hiperium.city.tasks.events.function.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.events.function.utils.TestsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * To execute this Integration Test, execute the following command:
 * <p>
 *     mvn verify -f src/city-tasks-events-function/pom.xml
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class EventsFunctionApplicationITest extends AbstractContainerBaseTest {

    public static final String FUNCTION_NAME = "city-tasks-events-function";

    @Autowired
    private DynamoDbClient dynamoDbClient;

    private static LambdaClient lambdaClient;

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        String lambdaEndpoint = LOCALSTACK_CONTAINER.getEndpoint().toString();
        log.debug("Lambda endpoint: {}", lambdaEndpoint);
        lambdaClient = LambdaClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .endpointOverride(new URI(lambdaEndpoint))
                .build();

        CreateFunctionRequest functionRequest = getFunctionCreationRequest();
        lambdaClient.createFunction(functionRequest);
        log.debug("Waiting for Lambda function to be Active....");
        TestsUtil.waitForFunctionToBeActive(lambdaClient, FUNCTION_NAME);
    }

    @Test
    @Order(1)
    @DisplayName("Wait for DynamoDB to be ready")
    void givenEventsTable_whenCreated_mustNotThrownError() {
        TestsUtil.waitForTableToBecomeActive(this.dynamoDbClient);
        assertTrue(true);
    }

    @Order(2)
    @ParameterizedTest
    @DisplayName("Send events to Lambda function")
    @ValueSource(strings = {
            "src/test/resources/events/lambda-event-valid-detail.json",
            "src/test/resources/events/lambda-event-invalid-detail.json",
            "src/test/resources/events/lambda-event-without-detail.json"})
    void givenEventList_whenInvokeLambdaFunction_thenReturnOkResponse(String jsonFilePath) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(FUNCTION_NAME)
                .payload(TestsUtil.getFileAsSdkBytes(jsonFilePath))
                .build();
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
        assertEquals(200, invokeResponse.statusCode());
    }

    private static CreateFunctionRequest getFunctionCreationRequest() {
        String projectVersion = TestsUtil.getProjectVersion();
        return CreateFunctionRequest.builder()
                .functionName(FUNCTION_NAME)
                .runtime("java17")
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
                .role("arn:aws:iam::000000000000:role/lambda-role")
                .architectures(Architecture.ARM64)
                .packageType(PackageType.ZIP)
                .code(builder -> builder.zipFile(TestsUtil
                        .getFileAsSdkBytes("target/city-tasks-events-function-" + projectVersion + ".jar")))
                .build();
    }
}
