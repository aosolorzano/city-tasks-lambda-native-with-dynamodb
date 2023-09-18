#####################################################################################
############################# Stage 1: Builder Image ################################
#####################################################################################
FROM hiperium/native-image-builder AS builder

COPY pom.xml                        pom.xml
COPY src/city-tasks-api/pom.xml     src/city-tasks-api/pom.xml
RUN mvn dependency:go-offline -B -f src/city-tasks-api/pom.xml
COPY src/city-tasks-api/src         src/city-tasks-api/src

RUN mvn -T 4C clean native:compile -Pnative -DskipTests -f src/city-tasks-api/pom.xml -Ddependency-check.skip=true

#####################################################################################
######################### Stage 2: Native Application Image #########################
###### https://github.com/oracle/container-images/pkgs/container/oraclelinux ########
#####################################################################################
FROM oraclelinux:9-slim
COPY --from=builder /workspace/src/city-tasks-api/target/city-tasks-api application

EXPOSE 8080
CMD ["sh", "-c", "./application"]
