package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.service.ImageService;
import com.sda.eventapp.web.mvc.form.EventForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Controller
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping({"/event"})
public class EventController {
    private final EventService eventService;
    private final ImageService imageService;

    private final IAuthenticationFacade authenticationFacade;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("event", new EventForm());
        return "create-event";
    }

    // TODO #005: Maybe MultipartFile parameter can have some annotation to valid it? Ex. valid file extension.
    // If we'll choose to do so, we can handle file extension error via if (errors.hasErrors()), I think.
    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("event") @Valid EventForm form, Errors errors,
                               @RequestParam MultipartFile file, RedirectAttributes ra) {
        if (errors.hasErrors()) {
            return "create-event";
        }

        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        // Not sure if this should be handled by ImageService.
        // But check should be here, to have proper redirect.

        // If file is uploaded (is not empty) and file is not an image.
        if (!file.isEmpty() && !imageService.isImage(file)) {
            ra.addFlashAttribute("wrongFileExtension",
                    imageService.wrongFileExtensionMessage());
            return "redirect:/event/create";
        }
        eventService.save(form, loggedUser, file);
        return "redirect:/my-events";
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String handleImageUploadError(RedirectAttributes ra) {
        ra.addFlashAttribute("uploadError",
                "You could not upload file bigger than " + maxFileSize);
        return "redirect:/event/create";
    }

    @GetMapping("/update/{id}")
    public String update(ModelMap model, @PathVariable Long id) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();
        if (loggedUser.getId() != eventService.findOwnerIdByEventId(id)
                && !loggedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ResponseStatusException(FORBIDDEN, "ACCESS DENIED - ONLY OWNER CAN UPDATE THIS EVENT");
        }
        if (eventService.findByIdFetchOwnerFetchUsers(id).getStartingDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT UPDATE AN EVENT AFTER ITS START DATE");
        }

        EventView eventToUpdate = eventService.findEventViewById(id);
        model.addAttribute("event", eventToUpdate);
        model.addAttribute("eventImage", eventToUpdate.getImage());


        return "update-event";
    }

    @PostMapping("/update")
    public String handleUpdate(@ModelAttribute("event") @Valid EventForm form, Errors errors,
                               @RequestParam MultipartFile file, RedirectAttributes ra, ModelMap map) {
        map.addAttribute("eventImage", eventService.findById(form.getId()).getImage());

        if (errors.hasErrors()) {
            return "update-event";
        }

        // Not sure if this should be handled by ImageService.
        // But check should be here, to have proper redirect.

        // If file is uploaded (is not empty) and file is not an image.
        if (!file.isEmpty() && !imageService.isImage(file)) {
            ra.addFlashAttribute("wrongFileExtension",
                    imageService.wrongFileExtensionMessage());
            return "redirect:/event/update";
        }
        eventService.update(form, file);
        return "redirect:/my-events";
    }
}