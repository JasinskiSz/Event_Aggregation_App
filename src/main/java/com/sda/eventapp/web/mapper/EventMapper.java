package com.sda.eventapp.web.mapper;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.web.dto.EventWithBasicData;

import java.util.List;

public class EventMapper {
    public static List<EventWithBasicData> toWebpage(List<Event> events) {
        return events.stream()
                .map(event -> EventWithBasicData
                        .builder()
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .build())
                .toList();
    }
}
