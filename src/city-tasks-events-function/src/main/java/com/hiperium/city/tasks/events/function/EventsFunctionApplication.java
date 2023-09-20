package com.hiperium.city.tasks.events.function;

import com.hiperium.city.tasks.events.function.converters.EventBridgeMessageConverter;
import com.hiperium.city.tasks.events.function.models.Event;
import com.hiperium.city.tasks.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.function.utils.PropertiesUtil;
import com.hiperium.city.tasks.events.function.models.EventsResponse;
import com.hiperium.city.tasks.events.function.models.TaskEventDetail;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;

@SpringBootApplication
@RegisterReflectionForBinding({EventBridgeCustomEvent.class, TaskEventDetail.class, Event.class, EventsResponse.class})
public class EventsFunctionApplication {

    public static void main(String[] args) {
        PropertiesUtil.setApplicationProperties();
        SpringApplication.run(EventsFunctionApplication.class, args);
    }

    @Bean
    public MessageConverter customMessageConverter() {
        return new EventBridgeMessageConverter();
    }
}
