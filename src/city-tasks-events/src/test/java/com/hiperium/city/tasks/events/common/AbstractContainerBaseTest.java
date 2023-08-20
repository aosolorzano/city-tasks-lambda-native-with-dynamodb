package com.hiperium.city.tasks.events.common;

import com.hiperium.city.tasks.events.utils.FunctionUtil;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Slf4j
public abstract class AbstractContainerBaseTest {

    protected static final LocalStackContainer LOCALSTACK_CONTAINER;

    // Singleton containers.
    // See: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    static {
        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(
                        LocalStackContainer.Service.LAMBDA,
                        LocalStackContainer.Service.DYNAMODB)
                .withCopyToContainer(MountableFile.forClasspathResource("infra-setup.sh"),
                        "/etc/localstack/init/ready.d/events-setup.sh")
                .withCopyToContainer(MountableFile.forClasspathResource("data-setup.json"),
                        "/var/lib/localstack/events-data.json");
        LOCALSTACK_CONTAINER.start();

        System.setProperty("aws.region", LOCALSTACK_CONTAINER.getRegion());
        System.setProperty("aws.accessKeyId", LOCALSTACK_CONTAINER.getAccessKey());
        System.setProperty("aws.secretAccessKey", LOCALSTACK_CONTAINER.getSecretKey());
        System.setProperty(FunctionUtil.AWS_ENDPOINT_OVERRIDE_PROPERTY,
                LOCALSTACK_CONTAINER.getEndpoint().toString());
    }
}
