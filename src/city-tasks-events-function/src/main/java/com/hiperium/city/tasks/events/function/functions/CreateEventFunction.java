package com.hiperium.city.tasks.events.function.functions;

import com.hiperium.city.tasks.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.function.models.EventsResponse;
import com.hiperium.city.tasks.events.function.services.EventService;
import com.hiperium.city.tasks.events.function.utils.BeansValidationUtil;
import com.hiperium.city.tasks.events.function.utils.FunctionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class CreateEventFunction implements Function<EventBridgeCustomEvent, EventsResponse> {

    private final EventService eventService;

    public CreateEventFunction(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public EventsResponse apply(EventBridgeCustomEvent event) {
        log.debug("handleRequest(): {}", event);
        BeansValidationUtil.validateBean(event);
        this.eventService.createEvent(event);
        return FunctionsUtil.getSuccessResponse();
    }
}
