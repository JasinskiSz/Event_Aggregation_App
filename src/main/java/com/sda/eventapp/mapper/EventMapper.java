package com.sda.eventapp.mapper;

import com.sda.eventapp.dto.EventApi;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.EventForm;
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

    public EventView[] toEventViewArray(List<Event> events) {
        List<EventView> eventViews = this.toEventViewList(events);
        return eventViews.toArray(new EventView[0]);
    }

    public List<EventApi> toEventApiList(List<Event> events) {
        return events.stream()
                .map(event -> EventApi.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .startingDateTime(event.getStartingDateTime())
                        .endingDateTime(event.getEndingDateTime())
                        .image(event.getImage())
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