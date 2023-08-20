package com.hiperium.city.tasks.events.mapper;

import com.hiperium.city.tasks.events.model.Event;
import com.hiperium.city.tasks.events.model.TaskEventDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    Event fromTaskEventDetail(TaskEventDetail taskEventDetail);
}
