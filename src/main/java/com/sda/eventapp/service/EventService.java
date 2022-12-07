package com.sda.eventapp.service;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
    }

    public Event updateByModify(Event event, CreateEventForm form) {
        event = eventRepository.findById(form.getId()).
                orElseThrow(() -> new RuntimeException("Event not found"));
        event.setId((form.getId()));
        event.setTitle((form.getTitle()));
        event.setDescription(form.getDescription());
        event.setStartingDateTime(form.getStartingDateTime());
        event.setEndingDateTime(form.getEndingDateTime());

        return eventRepository.save(event);

    }
}