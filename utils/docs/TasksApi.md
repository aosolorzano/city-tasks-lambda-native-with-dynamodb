## city-tasks-api

This project contains source code and supporting files for a containerized application that you can deploy with the Copilot CLI. It includes the following files and folders.

- src/main                     - Code for the container application.
- src/test                     - Integration tests for the application code.
- src/main/resources           - Spring Boot configuration files.

This application manages Quartz Jobs with the help of Spring Webflux and Spring Native.

### Running Locally using Docker Compose.

Use this option if you want to explore more features such as running your tests in a native image.

*IMPORTANT:* The GraalVM `native-image` compiler should be installed and configured on your machine.
*IMPORTANT:* If you're experiencing JMV memory issues, execute the following commands to increase the JVM memory and execute Maven with more CPU cores:
```bash
export _JAVA_OPTIONS="-Xmx8g -Xms4g"
mvn -T 2C clean native:compile -Pnative -DskipTests -f src/city-tasks-api/pom.xml -Ddependency-check.skip=true
```

Package and install the Lambda Function:
```bash
mvn clean install -DskipTests -f src/city-tasks-events/pom.xml
```

Deploy the dependent services using Docker Compose:
```bash
docker compose up tasks-localstack tasks-postgres
```

Open a new terminal window and export the following environment variables:
*IMPORTANT:* Replace the `<your_cognito_region>` and `<your_cognito_user_pool_id>` with your own values.
```bash
export SPRING_PROFILES_ACTIVE=dev
export CITY_TASKS_DB_CLUSTER_SECRET='{"dbClusterIdentifier":"city-tasks-db-cluster","password":"postgres123","dbname":"CityTasksDB","engine":"postgres","port":5432,"host":"localhost","username":"postgres"}'
export CITY_IDP_ENDPOINT='https://cognito-idp.<your_cognito_region>.amazonaws.com/<your_cognito_user_pool_id>'
export CITY_TASKS_TIME_ZONE='-05:00'
export AWS_DEFAULT_REGION='ap-southeast-2'
export AWS_ACCESS_KEY_ID='DUMMY'
export AWS_SECRET_ACCESS_KEY='DUMMY'
export AWS_ENDPOINT_OVERRIDE='http://localhost:4566'
```
Then, run the Spring Boot microservice from the project's root directory:
```bash
mvn clean spring-boot:run -DskipTests -f src/city-tasks-api/pom.xml
```

### Running Locally using Native Executable.

The process is the same as before, but you need to compile the native executable instead using the `spring-boot:run` directive:
```bash
mvn clean native:compile -Pnative -DskipTests -f src/city-tasks-api/pom.xml
```
Then, you can run the native executable as follows:
```bash
sh -c "src/city-tasks-api/target/city-tasks-api"
```

### Running Integration Tests in a Native Image.

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application:
```bash
mvn -T 2C test -PnativeTest -f src/city-tasks-api/pom.xml
```

### AWS Copilot CLI Helpful Commands.

List all of your AWS Copilot applications.
```bash
copilot app ls
```

Show information about the environments and services in your application.
```bash
copilot app show
```

Show information about your environments.
```bash
copilot env ls
```

List of all the services in an application.
```bash
copilot svc ls
```

Show service status.
```bash
copilot svc status
```

Show information about the service, including endpoints, capacity and related resources.
```bash
copilot svc show
```

Show logs of a deployed service.
```bash
copilot svc logs        \
    --app city-tasks    \
    --name api          \
    --env dev           \
    --since 1h          \
    --follow
```
Start an interactive bash session with a task part of the service:
```bash
copilot svc exec        \
    --app city-tasks    \
    --name api          \
    --env dev
```
To delete and clean-up all created resources.
```bash
copilot app delete --yes
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.1.1/reference/html/native-image.html#native-image)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#web.reactive)
* [Testcontainers](https://www.testcontainers.org/)
* [Testcontainers Postgres Module Reference Guide](https://www.testcontainers.org/modules/databases/postgres/)
* [OAuth2 Authorization Server](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#web.security.oauth2.authorization-server)
* [OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Flyway Migration](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#howto.data-initialization.migration-tool.flyway)
* [Quartz Scheduler](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#io.quartz)
* [Validation](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#io.validation)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#actuator)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links
These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/htmlsingle/#aot)

## GraalVM Native Support
This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```bash
$ ./mvnw spring-boot:build-image -Pnative -DskipTests
```

Then, you can run the app like any other container:

```bash
$ docker run --rm city-tasks-api:1.6.0
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

**NOTE:** GraalVM 22.3+ is required.

To create the executable, run the following goal:

```bash
$ ./mvnw native:compile -Pnative -DskipTests
```

Then, you can run the app as follows:
```bash
$ target/city-tasks-api
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```bash
$ ./mvnw test -PnativeTest
```
