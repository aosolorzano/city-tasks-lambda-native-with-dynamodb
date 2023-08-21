package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.events.utils.DynamoDBUtil;
import com.hiperium.city.tasks.events.utils.TestsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * To invoke this Integration Test, run the following command:
 * <p>
 *     mvn verify -f src/city-tasks-events/pom.xml
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationHandlerITest extends AbstractContainerBaseTest {

    private static LambdaClient lambdaClient;

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                .functionName(TestsUtil.FUNCTION_NAME)
                .runtime("java17")
                .handler("com.hiperium.city.tasks.events.ApplicationHandler::handleRequest")
                .role("arn:aws:iam::000000000000:role/lambda-role")
                .architectures(Architecture.ARM64)
                .packageType(PackageType.ZIP)
                .code(builder -> builder.zipFile(TestsUtil.getFileAsSdkBytes("target/city-tasks-events-1.7.0.jar")))
                .build();

        String lambdaEndpoint = LOCALSTACK_CONTAINER.getEndpoint().toString();
        log.debug("Lambda endpoint: {}", lambdaEndpoint);
        lambdaClient = LambdaClient.builder()
                .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .endpointOverride(new URI(lambdaEndpoint))
                .build();
        lambdaClient.createFunction(functionRequest);
        log.debug("Waiting for Lambda function to be Active....");
        TestsUtil.waitForFunctionToBeActive(lambdaClient);

        DynamoDbAsyncClient dynamoDbAsyncClient = DynamoDBUtil.getDynamoDbClient();
        log.debug("Waiting for DynamoDB table to be created...");
        TestsUtil.waitForTableToBeCreated(dynamoDbAsyncClient);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/events/lambda-valid-event.json",
            "src/test/resources/events/lambda-event-invalid-detail.json",
            "src/test/resources/events/lambda-event-without-detail.json"})
    void givenEventList_whenInvokeLambdaFunction_thenReturnOkResponse(String jsonFilePath) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(TestsUtil.FUNCTION_NAME)
                .payload(TestsUtil.getFileAsSdkBytes(jsonFilePath))
                .build();
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
        assertEquals(200, invokeResponse.statusCode());
    }
}
