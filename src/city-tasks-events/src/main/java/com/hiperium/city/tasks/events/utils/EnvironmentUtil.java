package com.hiperium.city.tasks.events.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentUtil {

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
