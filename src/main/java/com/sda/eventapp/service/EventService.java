package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final static String IMAGES_PATH = "src/main/resources/static/images/";

    private final EventRepository repository;
    private final CommentService commentService;
    private final ImageService imageService;
    private final EventMapper mapper;

    public Event save(CreateEventForm form, User owner, MultipartFile file) {
        form.setOwner(owner);
        form.setImage(solveImage(file));
        return repository.save(mapper.toEvent(form));
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

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id " + id + " not found"));
    }

    public Event findByIdFetchOwnerFetchUsers(Long id) {
        return repository.findByIdFetchOwnerFetchUsers(id)
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

    public EventView findEventViewById(Long id) {
        return mapper.toEventView(this.findById(id));
    }

    public List<EventView> findAllEventViews() {
        return mapper.toEventViewList(repository.findAll());
    }

    public List<EventView> findAllEventViews(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        if (title == null || title.equals("") || title.isBlank()) {
            return mapper.toEventViewList(findAllWithFilters(futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        } else {
            return mapper.toEventViewList(findAllWithFilters(title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        }
    }

    public List<EventView> findEventViewsByDateRange(LocalDateTime start, LocalDateTime end) {
        return mapper.toEventViewList(repository.findAllEventByDateRange(start, end));
    }

    public List<CommentView> findCommentViewsByEventId(Long eventId) {
        return commentService.findCommentViewsByEventId(eventId);
    }

    public void saveComment(CreateCommentForm form, Long eventId, User loggedUser) {
        commentService.save(form, this.findById(eventId), loggedUser);
    }

    /**
     * Taking {@link org.springframework.web.multipart.MultipartFile} and checks its name if it's {@link java.lang.String#isBlank()} or null.
     * <br>
     * If false, it does {@link com.sda.eventapp.service.EventService#saveImageLocally(MultipartFile)}
     *
     * @param file should be checked before if its of required file extension
     * @return {@link com.sda.eventapp.model.Image} build from {@link org.springframework.web.multipart.MultipartFile}
     */
    private Image solveImage(MultipartFile file) {
        Image image;
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            image = getDefaultImage();
        } else {
            image = saveImageLocally(file);
        }
        return image;
    }

    private Image getDefaultImage() {
        return imageService.buildDefaultImage(
                Paths.get("").toAbsolutePath(),
                IMAGES_PATH);
    }

    /**
     * Creates directory in default location ->  {@link EventService#createImageDirectory()}.
     * <br>
     * Then builds image from file originalFilename -> {@link ImageService#buildImage(String
     * fileName, Path absolutePath, String contentRootPath)}
     * <br>
     * Then checks if image of this name already exists in repository. If true, it will randomize
     * its name suffix and will try again -> {@link EventService#randomizeFilename(String fileName)}
     *
     * @param file {@link org.springframework.web.multipart.MultipartFile} that is meant to be saved
     *             locally and built {@link com.sda.eventapp.model.Image} out of it
     * @return built and saved locally {@link com.sda.eventapp.model.Image}
     */
    private Image saveImageLocally(MultipartFile file) {
        if (createImageDirectory()) {
            log.debug("Directory created");
        }
        String originalFilename = file.getOriginalFilename();
        Image image = imageService.buildImage(originalFilename, Paths.get("").toAbsolutePath(), IMAGES_PATH);

        // TODO #004: This implementation of while loop is smelly. We should think about something else.
        while (imageService.existsByFilename(image.getFilename())) {
            image.setFilename(randomizeFilename(originalFilename));
        }

        Path fullPath = Paths.get(image.getPath() + image.getFilename());

        try {
            byte[] bytes = file.getBytes();
            Files.write(fullPath, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private String randomizeFilename(String filename) {
        String salt = RandomStringUtils.random(10, true, false);
        return salt + filename;
    }

    /**
     * Creates directory named after path: {@link com.sda.eventapp.service.EventService#IMAGES_PATH} if it doesn't already exist.
     *
     * @return true in the same manner as {@link java.io.File#mkdirs()}
     */
    private boolean createImageDirectory() {
        return new File(IMAGES_PATH).mkdirs();
    }

    public Event signUpForEvent(User user, Long eventId) {
        Event event = this.findByIdFetchOwnerFetchUsers(eventId);
        event.getUsers().add(user);
        return repository.save(event);
    }

    public Event signOutFromEvent(User user, Long eventId) {
        Event event = this.findByIdFetchOwnerFetchUsers(eventId);
        event.getUsers().remove(user);
        return repository.save(event);
    }

    /**
     * @param userId            id of {@link User} for whom event views should be found
     * @param participationType first filter from {@link com.sda.eventapp.filters.ParticipationType}
     * @param dateType          second filter from {@link com.sda.eventapp.filters.DateType}
     * @return A list of event views bound with user id filtered by participationType and dateType
     */
    public List<EventView> findAllEventViews(Long userId, String participationType, String dateType) {

        //Owned + Future
        if (participationType.equals("Owned Events") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findOwnedFutureEventsByOwner_Id(userId));
        }

        //Owned + FutureOngoing
        else if (participationType.equals("Owned Events") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findOwnedFutureAndOngoingEventsByOwner_Id(userId));
        }

        //Owned + Past
        else if (participationType.equals("Owned Events") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findOwnedPastEventsByOwner_Id(userId));
        }

        //Owned + All
        else if (participationType.equals("Owned Events") && dateType.equals("All")) {
            return mapper.toEventViewList(repository.findOwnedAllEventsByOwner_IdOrderByStartingDateTime(userId));
        }

        //Attended + Future
        else if (participationType.equals("Attended Events") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findAttendedFutureEventsById(userId));
        }

        //Attended + FutureOngoing
        else if (participationType.equals("Attended Events") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findAttendedFutureAndOngoingEventsById(userId));
        }

        //Attended + Past
        else if (participationType.equals("Attended Events") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findAttendedPastEventsById(userId));
        }

        //Attended + All
        else if (participationType.equals("Attended Events") && dateType.equals("All")) {
            return mapper.toEventViewList(repository.findAttendedAllEventsByUsers_IdOrderByStartingDateTime(userId));
        }

        //All + Future
        else if (participationType.equals("All Events") && dateType.equals("Future")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedFutureEventsById(userId));
        }

        //All + FutureOngoing
        else if (participationType.equals("All Events") && dateType.equals("Future and Ongoing")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedFutureAndOngoingEventsById(userId));
        }

        //All + Past
        else if (participationType.equals("All Events") && dateType.equals("Past")) {
            return mapper.toEventViewList(repository.findOwnedAndAttendedPastEventsById(userId));
        }

        //All + All
        else {
            return mapper.toEventViewList(repository.findOwnedAndAttendedAllEventsById(userId));
        }
    }
}