package com.sda.eventapp.web.mappers;

import com.sda.eventapp.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


//todo: change name indicating that it is mapping from database entity to object displayed on webpage?
//todo: or there can be multiple methods like toEntity or toWebpage?
@Builder
@Getter
@Setter
@AllArgsConstructor
public class EventMapper {
    public static Event toWebpage(Event event){
        return Event.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .build();
    }
    //todo: overloading? is it a good practice?
    public static List<Event> toWebpage(List<Event> events){
        return events.stream().map(event -> Event.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .build()).toList();
    }
}
