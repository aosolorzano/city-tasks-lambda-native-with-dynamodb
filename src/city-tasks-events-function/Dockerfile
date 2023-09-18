#####################################################################################
############################# Stage 1: Builder Image ################################
#####################################################################################
FROM hiperium/native-image-builder AS builder

COPY pom.xml                                 pom.xml
COPY src/city-tasks-events-function/pom.xml  src/city-tasks-events-function/pom.xml
RUN mvn dependency:go-offline -B -f          src/city-tasks-events-function/pom.xml
COPY src/city-tasks-events-function/src      src/city-tasks-events-function/src
COPY src/city-tasks-events-function/utils    src/city-tasks-events-function/utils

RUN mvn -T 4C clean native:compile -Pnative -DskipTests -f src/city-tasks-events-function/pom.xml -Ddependency-check.skip=true

#####################################################################################
######################## Stage 2: Native Application Image ##########################
#####################################################################################
FROM public.ecr.aws/lambda/provided:al2

COPY --from=builder /workspace/src/city-tasks-events-function/target/native-assembly.zip /workspace/apps/events-native-assembly.zip
COPY --from=builder /workspace/src/city-tasks-events-function/target/native ${LAMBDA_TASK_ROOT}
COPY --from=builder /workspace/src/city-tasks-events-function/utils/shell/bootstrap ${LAMBDA_RUNTIME_DIR}

RUN chmod +x ${LAMBDA_RUNTIME_DIR}/bootstrap

CMD [ "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest" ]
