package com.sda.eventapp.web.mapper;

import com.sda.eventapp.dto.EventWithBasicData;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.web.mvc.form.CreateEventForm;

import java.util.List;

public class EventMapper {
    public static EventWithBasicData toWebpage(Event event){
        return EventWithBasicData.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .image(event.getImage())
                .build();
    }

    public static List<EventWithBasicData> toWebpage(List<Event> events) {
        return events.stream()
                .map(event -> EventWithBasicData
                        .builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .build())
                .toList();
    }

    public static Event toEntity(CreateEventForm form, Image img) {
        return Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .startingDateTime(form.getStartingDateTime())
                .endingDateTime(form.getEndingDateTime())
                .image(img)
                .build();
    }
}
