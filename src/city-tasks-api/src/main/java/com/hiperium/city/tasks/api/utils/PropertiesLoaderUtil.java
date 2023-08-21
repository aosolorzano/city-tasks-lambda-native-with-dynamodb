package com.hiperium.city.tasks.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesLoaderUtil {

    private static final String JDBC_SQL_CONNECTION = "jdbc:postgresql://{0}:{1}/{2}";

    public static void loadProperties() throws JsonProcessingException {
        setDatasourceConnection();
        setIdentityProviderEndpoint();
        setApplicationTimeZone();
    }

    private static void setDatasourceConnection() throws JsonProcessingException {
        AuroraSecretsVO auroraSecretVO = EnvironmentUtil.getAuroraSecretVO();
        if (Objects.nonNull(auroraSecretVO)) {
            String sqlConnection = MessageFormat.format(JDBC_SQL_CONNECTION, auroraSecretVO.host(),
                    auroraSecretVO.port(), auroraSecretVO.dbname());
            log.debug("JDBC Connection: {}", sqlConnection);
            // Set Datasource connection for JPA.
            System.setProperty("spring.datasource.url", sqlConnection);
            System.setProperty("spring.datasource.username", auroraSecretVO.username());
            System.setProperty("spring.datasource.password", auroraSecretVO.password());
            // Set Datasource connection for Quartz.
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.URL", sqlConnection);
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.user", auroraSecretVO.username());
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.password", auroraSecretVO.password());
        }
    }

    private static void setIdentityProviderEndpoint() {
        String idpEndpoint = EnvironmentUtil.getIdpEndpoint();
        if (Objects.nonNull(idpEndpoint) && !idpEndpoint.isEmpty()) {
            log.debug("IdP URI: {}", idpEndpoint);
            System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", idpEndpoint);
        }
    }

    private static void setApplicationTimeZone() {
        String timeZoneId = EnvironmentUtil.getTimeZone();
        if (Objects.nonNull(timeZoneId) && !timeZoneId.isEmpty()) {
            log.debug("Time Zone: {}", timeZoneId);
            System.setProperty("city.tasks.time.zone", timeZoneId);
        }
    }
}
