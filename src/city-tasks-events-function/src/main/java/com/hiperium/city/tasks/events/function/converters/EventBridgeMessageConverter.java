package com.hiperium.city.tasks.events.function.converters;

import com.hiperium.city.tasks.events.function.utils.FunctionsUtil;
import com.hiperium.city.tasks.events.function.models.EventBridgeEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

public class EventBridgeMessageConverter extends AbstractMessageConverter {

    public EventBridgeMessageConverter() {
        super(new MimeType("application", "json"));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return (EventBridgeEvent.class.equals(clazz));
    }

    @Override
    public Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();
        return (payload instanceof EventBridgeEvent ? payload :
                FunctionsUtil.unmarshal((byte[]) payload, EventBridgeEvent.class));
    }
}
