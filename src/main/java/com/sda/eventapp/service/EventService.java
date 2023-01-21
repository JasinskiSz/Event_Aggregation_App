package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventApiWrapper;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import com.sda.eventapp.web.mvc.form.EventForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final CommentService commentService;
    private final ImageService imageService;
    private final EventMapper mapper;

    public Event save(EventForm form, User owner, MultipartFile file) {
        form.setOwner(owner);
        form.setImage(imageService.solveImage(file));
        return repository.save(mapper.toEvent(form));
    }

    public Event update(EventForm form, MultipartFile file) {
        Event event = this.findById(form.getId());

        form.setOwner(event.getOwner());

        if (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank()) {
            // if new file was uploaded
            form.setImage(imageService.solveImage(file));
        } else {
            // otherwise get image from db
            form.setImage(event.getImage());
        }

        return repository.save(mapper.toEvent(form));
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
    }

    public Long findOwnerIdByEventId(Long eventId) {
        return this.findById(eventId).getOwner().getId();
    }

    public Event findByIdFetchOwnerFetchUsersFetchImage(Long id) {
        return repository.findByIdFetchOwnerFetchUsersFetchImage(id)
                .orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
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
            return repository.findAllEvents();
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

    public EventView findEventViewById(Long id) {
        return mapper.toEventView(this.findByIdFetchOwnerFetchUsersFetchImage(id));
    }

    public List<EventView> findAllEventViews(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        if (title == null || title.equals("") || title.isBlank()) {
            return mapper.toEventViewList(findAllWithFilters(futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        } else {
            return mapper.toEventViewList(findAllWithFilters(title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        }
    }

    public EventApiWrapper getEventApiWrapperWithEventsInDateRange(LocalDateTime start, LocalDateTime end) {
        return new EventApiWrapper(
                mapper.toEventApiList(
                        repository.findAllEventByDateRange(start, end))
        );
    }

    public void saveComment(CreateCommentForm form, Long eventId, User loggedUser) {
        commentService.save(form, this.findById(eventId), loggedUser);
    }

    public Event signUpForEvent(User user, Long eventId) {
        Event event = this.findByIdFetchOwnerFetchUsersFetchImage(eventId);
        if (event.getStartingDateTime().isAfter(LocalDateTime.now())) {
            event.getUsers().add(user);
            repository.save(event);
        }
        return event;
    }

    public Event signOutFromEvent(User user, Long eventId) {
        Event event = this.findByIdFetchOwnerFetchUsersFetchImage(eventId);
        if (event.getStartingDateTime().isAfter(LocalDateTime.now())) {
            event.getUsers().remove(user);
            repository.save(event);
        }
        return event;
    }

    /**
     * @param userId            id of {@link User} for whom event views should be found
     * @param participationType first filter from {@link com.sda.eventapp.filters.ParticipationType}
     * @param dateType          second filter from {@link com.sda.eventapp.filters.DateType}
     * @return A list of event views bound with user id filtered by participationType and dateType
     */
    public List<EventView> findAllEventViews(Long userId, String participationType, String dateType) {

        //Owned + Future
        if (participationType.equals("Owned") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findOwnedFutureEventsByOwner_Id(userId));
        }

        //Owned + FutureOngoing
        else if (participationType.equals("Owned") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findOwnedFutureAndOngoingEventsByOwner_Id(userId));
        }

        //Owned + Past
        else if (participationType.equals("Owned") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findOwnedPastEventsByOwner_Id(userId));
        }

        //Owned + All
        else if (participationType.equals("Owned") && dateType.equals("All")) {
            return mapper.toEventViewList(repository.findOwnedAllEventsByOwner_IdOrderByStartingDateTime(userId));
        }

        //Attended + Future
        else if (participationType.equals("Attended") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findAttendedFutureEventsById(userId));
        }

        //Attended + FutureOngoing
        else if (participationType.equals("Attended") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findAttendedFutureAndOngoingEventsById(userId));
        }

        //Attended + Past
        else if (participationType.equals("Attended") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findAttendedPastEventsById(userId));
        }

        //Attended + All
        else if (participationType.equals("Attended") && dateType.equals("All")) {
            return mapper.toEventViewList(repository.findAttendedAllEventsByUsers_IdOrderByStartingDateTime(userId));
        }

        //All + Future
        else if (participationType.equals("All") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedFutureEventsById(userId));
        }

        //All + FutureOngoing
        else if (participationType.equals("All") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedFutureAndOngoingEventsById(userId));
        }

        //All + Past
        else if (participationType.equals("All") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedPastEventsById(userId));
        }

        //All + All
        else {
            return mapper.toEventViewList(repository.findOwnedAndAttendedAllEventsById(userId));
        }
    }

    public EventApiWrapper getEventApiWrapperWithAllFutureEvents() {
        return new EventApiWrapper(
                mapper.toEventApiList(
                        repository.findAllFutureEvents()
                )
        );
    }
}