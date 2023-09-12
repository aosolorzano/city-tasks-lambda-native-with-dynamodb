package com.hiperium.city.tasks.events.utils;

import com.hiperium.city.tasks.events.models.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.State;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static void waitForTableToBecomeActive(final DynamoDbAsyncClient dynamoDbAsyncClient) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))         // maximum wait time
                .pollInterval(Duration.ofSeconds(1))    // check every second
                .until(() -> {
                    CompletableFuture<DescribeTableResponse> future = dynamoDbAsyncClient
                            .describeTable(DescribeTableRequest
                                    .builder()
                                    .tableName(Event.TABLE_NAME)
                                    .build());
                    try {
                        return TableStatus.ACTIVE.equals(future.get().table().tableStatus());
                    } catch (InterruptedException | ExecutionException e) {
                        return false;
                    }
                });
    }

    public static void waitForFunctionToBeActive(LambdaClient lambdaClient, String functionName) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))         // maximum wait time
                .pollInterval(Duration.ofSeconds(1))    // check every second
                .until(() -> {
                    GetFunctionConfigurationRequest request = GetFunctionConfigurationRequest.builder()
                            .functionName(functionName)
                            .build();
                    State state = lambdaClient.getFunctionConfiguration(request).state();
                    return State.ACTIVE.equals(state);
                });
    }

    public static SdkBytes getFileAsSdkBytes(final String filePath) {
        Path path = Paths.get(filePath);
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            return SdkBytes.fromByteArray(fileBytes);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file: " + filePath, e);
        }
    }

    public static String getProjectVersion() {
        String projectVersion = "1.0.0";
        File pomFile = new File("pom.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("version");
            if (nList.getLength() > 0) {
                Element versionElement = (Element) nList.item(0);
                projectVersion = versionElement.getTextContent();
                log.info("Project version: {}", projectVersion);
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return projectVersion;
    }
}
