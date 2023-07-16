package com.hiperium.city.tasks.events.common;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractContainerBaseTest {

    protected static final String FUNCTION_NAME = "city-tasks-events";
    protected static final LocalStackContainer LOCALSTACK_CONTAINER;
    protected static LambdaClient lambdaClient;

    public static final String CONTAINER_JAR_PATH = "/tmp/localstack/city-tasks-events.jar";

    static {
        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.LAMBDA)
                .withCopyToContainer(
                        MountableFile.forHostPath(new File("target/city-tasks-events-1.6.0.jar").getPath()),
                        CONTAINER_JAR_PATH);
        LOCALSTACK_CONTAINER.start();
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException, URISyntaxException {
        Container.ExecResult lambdaCreation = LOCALSTACK_CONTAINER.execInContainer(
                "awslocal", "lambda", "create-function",
                "--function-name", FUNCTION_NAME,
                "--runtime", "java17",
                "--handler", "com.hiperium.city.tasks.events.ApplicationHandler::handleRequest",
                "--region", LOCALSTACK_CONTAINER.getRegion(),
                "--role", "arn:aws:iam::000000000000:role/lambda-role",
                "--zip-file", "fileb://" + CONTAINER_JAR_PATH,
                "--environment", "Variables={AWS_ACCESS_KEY_ID=" + LOCALSTACK_CONTAINER.getAccessKey() + ",AWS_SECRET_ACCESS_KEY=" + LOCALSTACK_CONTAINER.getSecretKey() + "}"
        );
        String lambdaEndpoint = LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.LAMBDA).toString();
        lambdaClient = LambdaClient.builder()
                .region(Region.of(LOCALSTACK_CONTAINER.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(LOCALSTACK_CONTAINER.getAccessKey(), LOCALSTACK_CONTAINER.getSecretKey())
                ))
                .endpointOverride(new URI(lambdaEndpoint))
                .build();
    }
}
