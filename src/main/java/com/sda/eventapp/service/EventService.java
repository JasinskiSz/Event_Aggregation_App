package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final EventRepository repository;
    private final CommentService commentService;
    private final EventMapper mapper;

    private final ImageService imageService;

    public Event save(CreateEventForm form, Image image) {
        return repository.save(mapper.toEvent(form, image));
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
            return mapper.toEventViewList(findAllWithFilters(futureEventsFilter, ongoingEventsFilter, pastEventsFilter));
        } else {
            return mapper.toEventViewList(findAllWithFilters(title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter));

        }
    }

    public List<CommentView> findCommentViewsByEventId(Long id) {
        return commentService.findCommentViewsByEventId(id);
    }

    public void saveComment(CreateCommentForm form, Long id) {
        commentService.save(form, this.findById(id));
    }

    private static String prepareImageFilenameIfAlreadyExists(Image image, String originalFilename) {
        StringBuilder newNameBuilder = new StringBuilder();
        newNameBuilder.append(RandomStringUtils.random(10, true, false));
        newNameBuilder.append(originalFilename);
        originalFilename = newNameBuilder.toString();
        image.setFileName(originalFilename);
        return originalFilename;
    }

    public String createEventWithoutPhoto(CreateEventForm form) {
        String folder = "/src/main/resources/static/images/";
        Path currentPath = Paths.get(""); //on Windows Paths.get(".")
        Path absolutePath = currentPath.toAbsolutePath();
        Image image = imageService.buildDefaultImage(folder, absolutePath);
        save(form, image);
        return "index";
    }

    public String createEventWithPhoto(CreateEventForm form, MultipartFile img, RedirectAttributes ra) {
        String folderForNewDirectory = "src/main/resources/static/images/";
        String folder = "/src/main/resources/static/images/";
        String extension = FilenameUtils.getExtension(img.getOriginalFilename());
        if (!(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png"))) {
            ra.addFlashAttribute("wrongExtension", "You must upload jpg or png file");
            return "redirect:/event/create";
        }
        try {
            createDirectoryIfNotExist(folderForNewDirectory);
            saveEvent(form, img, folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "index";
    }

    private void saveEvent(CreateEventForm form, MultipartFile img, String folder) throws IOException {
        Path currentPath = Paths.get(""); //on Windows Paths.get(".")
        Path absolutePath = currentPath.toAbsolutePath();
        byte[] bytes = img.getBytes();
        String originalFilename = img.getOriginalFilename();
        Image image = imageService.buildImage(img, folder, absolutePath);

        while (imageService.checkImageByFileName(originalFilename)) {
            originalFilename = prepareImageFilenameIfAlreadyExists(image, originalFilename);
        }
        Path fullPath = Paths.get(image.getPath() + originalFilename);
        Files.write(fullPath, bytes);

        save(form, image);
    }

    private void createDirectoryIfNotExist(String folderForNewDirectory) {
        File directory = new File(folderForNewDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


}