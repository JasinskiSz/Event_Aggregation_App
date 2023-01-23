package com.sda.eventapp.utils.mappers;

import com.sda.eventapp.dto.form.EventForm;
import com.sda.eventapp.dto.rest.api.EventApi;
import com.sda.eventapp.dto.views.EventView;
import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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
                        .usersNicknames(getUsersNicknames(event))
                        .ownerNickname(getOwnerNickname(event))
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
                .usersNicknames(getUsersNicknames(event))
                .ownerNickname(getOwnerNickname(event))
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

    public List<EventApi> toEventApiList(List<Event> events) {
        return events.stream()
                .map(event -> EventApi.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .imageUrl("http://localhost:8080/images/" + event.getImage().getFilename())
                        .attenders(getUsersNicknames(event))
                        .ownerNickname(getOwnerNickname(event))
                        .build())
                .toList();
    }

    private Set<String> getUsersNicknames(@NotNull Event event) {
        return event.getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }

    private String getOwnerNickname(@NotNull Event event) {
        return event.getOwner().getUsername();
    }
}