package com.sda.eventapp.service;

import com.sda.eventapp.dto.EventApiWrapper;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.dto.form.CreateCommentForm;
import com.sda.eventapp.dto.form.EventForm;
import com.sda.eventapp.filters.DateType;
import com.sda.eventapp.filters.ParticipationType;
import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.utils.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final CommentService commentService;
    private final ImageService imageService;
    private final FiltersService filtersService;
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

    public EventView findEventViewById(Long id) {
        return mapper.toEventView(this.findByIdFetchOwnerFetchUsersFetchImage(id));
    }

    public List<EventView> findAllEventViews(String title, boolean future, boolean ongoing, boolean past) {
        return mapper.toEventViewList(repository.findAll(filtersService.prepareSpecification(title, future, ongoing, past)));
    }

    public EventApiWrapper getEventApiWrapperWithEventsInDateRange(LocalDateTime start, LocalDateTime end) {
        return new EventApiWrapper(
                mapper.toEventApiList(
                        repository.findAllEventByDateRange(start, end))
        );
    }

    public void saveComment(CreateCommentForm form, Long eventId, User user) {
        commentService.save(form, this.findById(eventId), user);
    }

    public Event signUpForEvent(User user, Long eventId) {
        canSignUpForEvent(user, eventId);
        Event event = findByIdFetchOwnerFetchUsersFetchImage(eventId);
        if (event.getStartingDateTime().isAfter(LocalDateTime.now())) {
            event.getUsers().add(user);
            repository.save(event);
        }
        return event;
    }

    public Event signOutFromEvent(User user, Long eventId) {
        canSignOutFromEvent(user, eventId);
        Event event = findByIdFetchOwnerFetchUsersFetchImage(eventId);
        if (event.getStartingDateTime().isAfter(LocalDateTime.now())) {
            event.getUsers().remove(user);
            repository.save(event);
        }
        return event;
    }

    /**
     * @param userId            id of {@link User} for whom event views should be found
     * @param participationType first filter from {@link ParticipationType}
     * @param dateType          second filter from {@link DateType}
     * @return A list of event views bound with user id filtered by participationType and dateType
     */
    public List<EventView> findAllEventViews(Long userId, String participationType, String dateType) {
        return mapper.toEventViewList(repository.findAll(filtersService.prepareSpecification(userId, participationType, dateType)));
    }

    public EventApiWrapper getEventApiWrapperWithAllFutureEvents() {
        return new EventApiWrapper(
                mapper.toEventApiList(
                        repository.findAllFutureEvents()
                )
        );
    }

    private void canSignUpForEvent(User user, Long eventId) {
        if (findByIdFetchOwnerFetchUsersFetchImage(eventId).getOwner().getUsername().equals(user.getUsername())) {
            throw new ResponseStatusException(FORBIDDEN, "ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT");
        }
        if (findByIdFetchOwnerFetchUsersFetchImage(eventId).getStartingDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED");
        }
        if (findByIdFetchOwnerFetchUsersFetchImage(eventId).getUsers().contains(user)) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT IF ALREADY ASSIGNED");
        }
    }

    private void canSignOutFromEvent(User user, Long eventId) {
        if (findByIdFetchOwnerFetchUsersFetchImage(eventId).getOwner().getUsername().equals(user.getUsername())) {
            throw new ResponseStatusException(FORBIDDEN, "ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT");
        }
        if (findByIdFetchOwnerFetchUsersFetchImage(eventId).getStartingDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED");
        }
        if (!findByIdFetchOwnerFetchUsersFetchImage(eventId).getUsers().contains(user)) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT IF HAS NOT ASSIGNED");
        }
    }
}