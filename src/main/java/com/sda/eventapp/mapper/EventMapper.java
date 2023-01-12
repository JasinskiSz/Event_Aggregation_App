package com.sda.eventapp.mapper;

import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.EventForm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {
    public List<EventView> toEventViewList(List<Event> events) {
        return events.stream()
                .map(event -> EventView
                        .builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .image(event.getImage())
                        .usersNicknames(event.getUsers().stream()
                                .map(User::getUsername)
                                .collect(Collectors.toSet()))
                        .ownerNickname(event.getOwner().getUsername())
                        .build())
                .toList();
    }

    public EventView toEventView(Event event) {
        return EventView.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startingDateTime(event.getStartingDateTime())
                .endingDateTime(event.getEndingDateTime())
                .image(event.getImage())
                .usersNicknames(event.getUsers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toSet()))
                .ownerNickname(event.getOwner().getUsername())
                .build();
    }

    public Event toEvent(EventForm form) {
        return Event.builder()
                .id(form.getId())
                .title(form.getTitle())
                .description(form.getDescription())
                .startingDateTime(form.getStartingDateTime())
                .endingDateTime(form.getEndingDateTime())
                .image(form.getImage())
                .owner(form.getOwner())
                .build();
    }

    public List<EventView> toEventViewListClient(List<Event> events) {
        return events.stream()
                .map(event -> EventView
                        .builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .build())
                .toList();
    }
}