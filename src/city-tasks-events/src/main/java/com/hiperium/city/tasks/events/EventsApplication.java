package com.hiperium.city.tasks.events;

import com.hiperium.city.tasks.events.converters.EventBridgeMessageConverter;
import com.hiperium.city.tasks.events.models.Event;
import com.hiperium.city.tasks.events.models.EventBridgeEvent;
import com.hiperium.city.tasks.events.models.EventBridgeResponse;
import com.hiperium.city.tasks.events.models.TaskEventDetail;
import com.hiperium.city.tasks.events.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;

@Slf4j
@SpringBootApplication
@RegisterReflectionForBinding({EventBridgeEvent.class, TaskEventDetail.class, Event.class, EventBridgeResponse.class})
public class EventsApplication {

    public static void main(String[] args) {
        PropertiesUtil.setApplicationProperties();
        SpringApplication.run(EventsApplication.class, args);
    }

    @Bean
    public MessageConverter customMessageConverter() {
        return new EventBridgeMessageConverter();
    }
}
