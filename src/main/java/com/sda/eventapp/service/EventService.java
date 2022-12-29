package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final CommentService commentService;
    private final EventMapper mapper;

    public Event save(CreateEventForm form, User owner) {
        return repository.save(mapper.toEvent(form, owner));
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
    }

    public EventView findEventViewById(Long id) {
        return mapper.toEventView(findById(id));
    }

    public Event update(CreateEventForm form) {
        Event event = repository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setId((form.getId()));
        event.setTitle((form.getTitle()));
        event.setDescription(form.getDescription());
        event.setStartingDateTime(form.getStartingDateTime());
        event.setEndingDateTime(form.getEndingDateTime());

        return repository.save(event);
    }

    public List<EventView> findAllEventViews() {
        return mapper.toEventViewList(repository.findAll());
    }

    public List<EventView> findEventViewsByDateRange(LocalDateTime start, LocalDateTime end) {
        return mapper.toEventViewList(repository.findAllEventByDateRange(start, end));
    }

    private List<Event> findAllWithFilters(boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return repository.findAllFutureEvents();
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return repository.findAllOngoingEvents();
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return repository.findAllPastEvents();
        }

        //future past
        else if (futureEventsFilter && !ongoingEventsFilter) {
            return repository.findAllFutureAndPastEvents();
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter) {
            return repository.findAllOngoingAndPastEvents();
        }
        //future ongoing past
        else if (futureEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
        }
        //default - ongoing + future
        else {
            return repository.findAllOngoingAndFutureEvents();
        }
    }

    private List<Event> findAllWithFilters(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return repository.findAllFutureEventsByTitle(title);
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return repository.findAllOngoingEventsByTitle(title);
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return repository.findAllPastEventsByTitle(title);
        }
        //future past
        else if (futureEventsFilter && !ongoingEventsFilter) {
            return repository.findAllFutureAndPastEventsByTitle(title);
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter) {
            return repository.findAllOngoingAndPastEventsByTitle(title);
        }
        //future ongoing past
        else if (futureEventsFilter && pastEventsFilter) {
            return repository.findAllByTitle(title);
        }
        //default - ongoing + future
        else {
            return repository.findAllOngoingAndFutureEventsByTitle(title);
        }
    }

    public List<EventView> findAllEventViews(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        if (title == null || title.equals("") || title.isBlank()) {
            return mapper.toEventViewList(findAllWithFilters(title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        } else {
            return mapper.toEventViewList(findAllWithFilters(futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        }
    }

    public List<CommentView> findCommentViewsByEventId(Long id) {
        return commentService.findCommentViewsByEventId(id);
    }

    public void saveComment(CreateCommentForm form, Long id, User loggedUser) {
        commentService.save(form, this.findById(id), loggedUser);
    }
}