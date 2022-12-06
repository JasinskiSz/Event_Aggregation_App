package com.sda.eventapp.web.mapper;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.web.dto.EventDTO;

import java.util.List;


//todo: change name indicating that it is mapping from database entity to object displayed on webpage?
//todo: or there can be multiple methods like toEntity or toWebpage?

public class EventMapper {
    public static EventDTO toWebpage(Event event){
        return EventDTO.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .build();
    }
    //todo: overloading? is it a good practice?
    public static List<EventDTO> toWebpage(List<Event> events){
        return events.stream().map(event -> EventDTO.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .build()).toList();
    }
}
