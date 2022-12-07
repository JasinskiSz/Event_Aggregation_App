package com.sda.eventapp.web.mvc.mappers;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.web.mvc.form.CreateEventForm;

public class EventMapper {
    public static Event toEntity(CreateEventForm form) {
        return Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .startingDateTime(form.getStartingDateTime())
                .endingDateTime(form.getEndingDateTime())
                .build();
    }
}