package com.sda.eventapp.web.mapper;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.web.dto.EventView;

import java.util.List;

public class EventMapper {
    public List<EventView> from(List<Event> events) {
        return events.stream()
                .map(event -> EventView
                        .builder()
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .build())
                .toList();
    }
}
