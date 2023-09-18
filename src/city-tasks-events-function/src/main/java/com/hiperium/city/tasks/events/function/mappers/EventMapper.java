package com.hiperium.city.tasks.events.function.mappers;

import com.hiperium.city.tasks.events.function.models.Event;
import com.hiperium.city.tasks.events.function.models.TaskEventDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    Event fromTaskEventDetail(TaskEventDetail taskEventDetail);
}
