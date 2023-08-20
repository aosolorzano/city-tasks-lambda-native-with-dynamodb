#####################################################################################
########################## Stage 1: Docker Builder Image ############################
#####################################################################################
FROM amazoncorretto:17.0.7-al2023 AS builder
WORKDIR /workspace/app
COPY mvnw                              mvnw
COPY .mvn                              .mvn
COPY pom.xml                           pom.xml
COPY src/city-tasks-api/pom.xml        src/city-tasks-api/pom.xml
RUN ./mvnw dependency:go-offline -B -f src/city-tasks-api/pom.xml
COPY src/city-tasks-api/src            src/city-tasks-api/src

RUN ./mvnw clean package -DskipTests -f src/city-tasks-api/pom.xml
RUN mkdir -p src/city-tasks-api/target/dependency && \
    (cd src/city-tasks-api/target/dependency; jar -xf ../city-tasks-api-*.jar)

#####################################################################################
######################## Stage 2: Docker Application Image ##########################
#####################################################################################
FROM amazoncorretto:17.0.7-al2023-headless
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/src/city-tasks-api/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes    /app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib        /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF            /app/META-INF

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.hiperium.city.tasks.api.TasksApplication"]
