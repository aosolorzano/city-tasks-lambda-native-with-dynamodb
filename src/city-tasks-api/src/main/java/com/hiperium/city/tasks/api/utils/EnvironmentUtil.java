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

    public static String getAwsEndpointOverride() {
        String endpointOverride = System.getenv("AWS_ENDPOINT_OVERRIDE");
        if (Objects.isNull(endpointOverride) || endpointOverride.isBlank()) {
            log.debug("AWS_ENDPOINT_OVERRIDE not found. Using defaults.");
        }
        return endpointOverride;
    }
}
