package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
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

    public Event save(CreateEventForm form, MultipartFile file) {
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
        return mapper.toEventView(findById(id));
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

    public List<CommentView> findCommentViewsByEventId(Long id) {
        return commentService.findCommentViewsByEventId(id);
    }

    public Comment saveComment(CreateCommentForm form, Long id) {
        return commentService.save(form, this.findById(id));
    }

    private Image solveImage(MultipartFile file) {
        Image image;
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            image = getDefaultImage();
        } else {
            image = saveImageLocally(file);
        }
        return image;
    }

    public Image getDefaultImage() {
        return imageService.buildDefaultImage(
                Paths.get("").toAbsolutePath(),
                IMAGES_PATH);
    }

    public Image saveImageLocally(MultipartFile img) {
        if (createImageDirectory()) {
            log.debug("Directory created");
        }
        String originalFilename = img.getOriginalFilename();
        Image image = imageService.buildImage(originalFilename, Paths.get("").toAbsolutePath(), IMAGES_PATH);

        // TODO: This implementation of while loop is smelly. We should think about something else.
        while (imageService.checkImageByFileName(image.getFileName())) {
            prepareImageFileName(image, originalFilename);
        }

        Path fullPath = Paths.get(image.getPath() + originalFilename);

        try {
            byte[] bytes = img.getBytes();
            Files.write(fullPath, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private void prepareImageFileName(Image image, String originalFilename) {
        String salt = RandomStringUtils.random(10, true, false);
        image.setFileName(salt + originalFilename);
    }

    /**
     * Creates directory named after path: {@link com.sda.eventapp.service.EventService#IMAGES_PATH} if it doesn't already exist.
     *
     * @return true in the same manner as {@link java.io.File#mkdirs()}
     */
    private boolean createImageDirectory() {
        if (new File(IMAGES_PATH).mkdirs()) {
            log.info("Image directory created");
            return true;
        }
        return false;
    }
}