package com.sda.eventapp.service;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService{

    private final EventRepository eventRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
    }

    public Event updateByModify(CreateEventForm form) {
        Event event = eventRepository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setId((form.getId()));
        event.setTitle((form.getTitle()));
        event.setDescription(form.getDescription());
        event.setStartingDateTime(form.getStartingDateTime());
        event.setEndingDateTime(form.getEndingDateTime());

        return eventRepository.save(event);
    }

    public List<Event> findAllWithFilters(boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return eventRepository.findAllFutureEvents();
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return eventRepository.findAllOngoingEvents();
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return eventRepository.findAllPastEvents();
        }

        //future past
        else if (futureEventsFilter && !ongoingEventsFilter) {
            return eventRepository.findAllFutureAndPastEvents();
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter) {
            return eventRepository.findAllOngoingAndPastEvents();
        }
        //future ongoing past
        else if (futureEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAll().spliterator(), false).collect(Collectors.toList());
        }
        //default - ongoing + future
        else {
            return eventRepository.findAllOngoingAndFutureEvents();
        }
    }

    public List<Event> findAllByTitleWithFilters(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return eventRepository.findAllFutureEventsByTitle(title);
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return eventRepository.findAllOngoingEventsByTitle(title);
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return eventRepository.findAllPastEventsByTitle(title);
        }
        //future past
        else if (futureEventsFilter && !ongoingEventsFilter) {
            return eventRepository.findAllFutureAndPastEventsByTitle(title);
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter) {
            return eventRepository.findAllOngoingAndPastEventsByTitle(title);
        }
        //future ongoing past
        else if (futureEventsFilter && pastEventsFilter) {
            return eventRepository.findAllByTitle(title);
        }
        //default - ongoing + future
        else {
            return eventRepository.findAllOngoingAndFutureEventsByTitle(title);
        }
    }
}

