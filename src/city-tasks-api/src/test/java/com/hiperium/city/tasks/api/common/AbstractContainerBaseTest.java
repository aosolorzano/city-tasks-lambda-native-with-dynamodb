package com.hiperium.city.tasks.api.common;

import com.hiperium.city.tasks.api.utils.ContainersUtil;
import com.hiperium.city.tasks.api.utils.PropertiesLoaderUtil;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

public abstract class AbstractContainerBaseTest {

    protected static final String AUTHORIZATION = "Authorization";

    private static final KeycloakContainer KEYCLOAK_CONTAINER;
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static final LocalStackContainer LOCALSTACK_CONTAINER;

    private static AccessTokenResponse accessTokenResponse;

    // Singleton containers.
    // See: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    static {
        KEYCLOAK_CONTAINER = new KeycloakContainer()
                .withRealmImportFile("keycloak-realm.json");
        KEYCLOAK_CONTAINER.start();
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:14.4")
                .withUsername("postgres")
                .withPassword("postgres123")
                .withDatabaseName("CityTasksDB");
        POSTGRES_CONTAINER.start();
        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.DYNAMODB);
        LOCALSTACK_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        // SPRING SECURITY OAUTH2 JWT
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> KEYCLOAK_CONTAINER.getAuthServerUrl() + ContainersUtil.KEYCLOAK_REALM);
        // SPRING DATA JDBC CONNECTION
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> ContainersUtil.POSTGRESQL_DRIVER);
        // SPRING QUARTZ JDBC CONNECTION
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.URL",
                POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.user",
                POSTGRES_CONTAINER::getUsername);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.password",
                POSTGRES_CONTAINER::getPassword);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.driver",
                () -> ContainersUtil.POSTGRESQL_DRIVER);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.provider",
                () -> ContainersUtil.QUARTZ_DS_PROVIDER);
        // AWS REGION, CREDENTIALS, AND ENDPOINT-OVERRIDE
        // SDK Region provider looks for the 'aws.region' System Property first.
        registry.add("aws.region", LOCALSTACK_CONTAINER::getRegion);
        registry.add("aws.accessKeyId", LOCALSTACK_CONTAINER::getAccessKey);
        registry.add("aws.secretAccessKey", LOCALSTACK_CONTAINER::getSecretKey);
        registry.add(PropertiesLoaderUtil.AWS_ENDPOINT_OVERRIDE, () -> LOCALSTACK_CONTAINER.getEndpoint().toString());
    }

    @BeforeAll
    public static void beforeAllTests() {
        Keycloak keycloakClient = KEYCLOAK_CONTAINER.getKeycloakAdminClient();
        accessTokenResponse = keycloakClient.tokenManager().getAccessToken();
    }

    protected String getBearerAccessToken() {
        return "Bearer " + Objects.requireNonNull(accessTokenResponse).getToken();
    }

    protected static LocalStackContainer getLocalstackContainer() {
        return LOCALSTACK_CONTAINER;
    }
}
