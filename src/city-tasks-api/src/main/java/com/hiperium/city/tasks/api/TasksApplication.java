package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.config.hints.HibernateProxyHints;
import com.hiperium.city.tasks.api.config.hints.QuartzHints;
import com.hiperium.city.tasks.api.config.hints.ResourceBundleHints;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.dto.TaskEventDTO;
import com.hiperium.city.tasks.api.scheduler.execution.JobExecution;
import com.hiperium.city.tasks.api.utils.PropertiesLoaderUtil;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVO;
import com.hiperium.city.tasks.api.vo.AwsPropertiesVO;
import lombok.SneakyThrows;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.nio.charset.StandardCharsets;

@EnableWebFlux
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(AwsPropertiesVO.class)
@ImportRuntimeHints({QuartzHints.class, HibernateProxyHints.class, ResourceBundleHints.class})
@RegisterReflectionForBinding({AuroraSecretsVO.class, TaskEventDTO.class, ErrorDetailsDTO.class, JobExecution.class})
public class TasksApplication {

    @SneakyThrows(JsonProcessingException.class)
    public static void main(String[] args) {
        PropertiesLoaderUtil.loadProperties();
        SpringApplication.run(TasksApplication.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        return messageSource;
    }
}
