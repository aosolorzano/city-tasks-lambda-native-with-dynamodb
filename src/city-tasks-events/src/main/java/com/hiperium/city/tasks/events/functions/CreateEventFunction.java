package com.hiperium.city.tasks.events.functions;

import com.hiperium.city.tasks.events.models.EventBridgeEvent;
import com.hiperium.city.tasks.events.models.EventBridgeResponse;
import com.hiperium.city.tasks.events.services.EventsService;
import com.hiperium.city.tasks.events.utils.BeansValidationUtil;
import com.hiperium.city.tasks.events.utils.FunctionsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class CreateEventFunction implements Function<EventBridgeEvent, EventBridgeResponse> {

    private final EventsService eventsService;

    public CreateEventFunction(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Override
    public EventBridgeResponse apply(EventBridgeEvent event) {
        log.debug("handleRequest(): {}", event);
        BeansValidationUtil.validateBean(event);
        this.eventsService.createEvent(event.getDetail());
        return FunctionsUtil.getSuccessResponse();
    }
}
