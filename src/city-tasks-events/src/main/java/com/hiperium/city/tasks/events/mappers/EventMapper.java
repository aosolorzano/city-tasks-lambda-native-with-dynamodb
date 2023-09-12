package com.hiperium.city.tasks.events.mappers;

import com.hiperium.city.tasks.events.models.Event;
import com.hiperium.city.tasks.events.models.TaskEventDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    Event fromTaskEventDetail(TaskEventDetail taskEventDetail);
}
