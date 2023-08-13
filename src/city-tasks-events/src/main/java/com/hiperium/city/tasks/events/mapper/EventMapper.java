package com.hiperium.city.tasks.events.mapper;

import com.hiperium.city.tasks.events.model.Event;
import com.hiperium.city.tasks.events.model.TaskEventDetail;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapper {

    Event fromTaskEventDetail(TaskEventDetail taskEventDetail);
}
