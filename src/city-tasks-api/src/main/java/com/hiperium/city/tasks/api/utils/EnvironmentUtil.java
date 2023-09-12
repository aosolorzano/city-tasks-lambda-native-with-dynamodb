package com.hiperium.city.tasks.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentUtil {

    public static AuroraSecretsVO getAuroraSecretVO() throws JsonProcessingException {
        String auroraSecret = System.getenv("CITY_TASKS_DB_CLUSTER_SECRET");
        if (Objects.isNull(auroraSecret) || auroraSecret.isBlank()) {
            log.warn("CITY_TASKS_DB_CLUSTER_SECRET not found. Using defaults.");
            return null;
        }
        return new ObjectMapper().readValue(auroraSecret, AuroraSecretsVO.class);
    }

    public static String getIdpEndpoint() {
        String idpEndpoint = System.getenv("CITY_IDP_ENDPOINT");
        if (Objects.isNull(idpEndpoint) || idpEndpoint.isBlank()) {
            log.warn("CITY_IDP_ENDPOINT not found. Using defaults.");
        }
        return idpEndpoint;
    }

    public static String getTimeZone() {
        String timeZoneId = System.getenv("CITY_TASKS_TIME_ZONE");
        if (Objects.isNull(timeZoneId) || timeZoneId.isBlank()) {
            log.warn("CITY_TASKS_TIME_ZONE not found. Using defaults.");
        }
        return timeZoneId;
    }

    public static String getAwsEndpointOverrideURL() {
        String endpointOverrideURL = System.getenv("AWS_ENDPOINT_OVERRIDE");
        if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isBlank()) {
            log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                    "Set the 'AWS_ENDPOINT_OVERRIDE' variable to null or empty if you want to use the default AWS endpoint.");
        } else {
            String awsEndpointURL = System.getenv("AWS_ENDPOINT_URL");
            if (Objects.nonNull(awsEndpointURL) && !awsEndpointURL.isEmpty() && !awsEndpointURL.endsWith(".amazonaws.com")) {
                log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                        "Remove the 'AWS_ENDPOINT_URL' variable if you want to use the default AWS endpoint.");
            }
        }
        return endpointOverrideURL;
    }
}
