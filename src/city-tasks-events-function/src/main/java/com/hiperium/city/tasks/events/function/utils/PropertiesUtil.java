package com.hiperium.city.tasks.events.function.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtil {

    public static final String AWS_ENDPOINT_OVERRIDE_PROPERTY = "aws.endpoint-override";
    public static final String TIME_ZONE_ID_PROPERTY = "city.tasks.time.zone.id";

    public static void setApplicationProperties() {
        setApplicationTimeZoneId();
        setAwsEndpointOverride();
    }

    private static void setApplicationTimeZoneId() {
        String timeZoneId = EnvironmentUtil.getTimeZoneId();
        if (Objects.nonNull(timeZoneId) && !timeZoneId.isBlank()) {
            log.debug("Time Zone ID: {}", timeZoneId);
            System.setProperty(TIME_ZONE_ID_PROPERTY, timeZoneId);
        }
    }

    private static void setAwsEndpointOverride() {
        String endpointOverrideURL = System.getProperty(AWS_ENDPOINT_OVERRIDE_PROPERTY);
        if (Objects.isNull(endpointOverrideURL) || endpointOverrideURL.isBlank()) {
            endpointOverrideURL = EnvironmentUtil.getAwsEndpointOverrideURL();
            if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isEmpty()) {
                System.setProperty(AWS_ENDPOINT_OVERRIDE_PROPERTY, endpointOverrideURL);
            }
        } else if (!endpointOverrideURL.endsWith(".amazonaws.com")){
            log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                            "Set the '{}' System Property to null or empty if you want to use the default AWS endpoint.",
                    AWS_ENDPOINT_OVERRIDE_PROPERTY);
        }
    }
}
