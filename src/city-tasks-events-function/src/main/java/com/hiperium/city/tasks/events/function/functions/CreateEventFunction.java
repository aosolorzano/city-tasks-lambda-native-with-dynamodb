package com.hiperium.city.tasks.events.function.functions;

import com.hiperium.city.tasks.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.tasks.events.function.models.EventsResponse;
import com.hiperium.city.tasks.events.function.services.EventsService;
import com.hiperium.city.tasks.events.function.utils.BeansValidationUtil;
import com.hiperium.city.tasks.events.function.utils.FunctionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class CreateEventFunction implements Function<EventBridgeCustomEvent, EventsResponse> {

    private final EventsService eventsService;

    public CreateEventFunction(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Override
    public EventsResponse apply(EventBridgeCustomEvent event) {
        log.debug("handleRequest(): {}", event);
        BeansValidationUtil.validateBean(event);
        this.eventsService.createEvent(event);
        return FunctionsUtil.getSuccessResponse();
    }
}
