package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.service.ImageService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    // TODO #005: Maybe MultipartFile parameter can have some annotation to valid it? Ex. valid file extension.
    // If we'll choose to do so, we can handle file extension error via if (errors.hasErrors()), I think.
    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors,
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
        return "redirect:/home";
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String handleImageUploadError(RedirectAttributes ra) {
        ra.addFlashAttribute("uploadError",
                "You could not upload file bigger than " + maxFileSize);
        return "redirect:/event/create";
    }

    @GetMapping("/update/{id}")
    public String update(ModelMap model, @PathVariable Long id) {
        model.addAttribute("event", new CreateEventForm());
        model.addAttribute("event", eventService.findById(id));
        return "update-event";
    }

    @PostMapping("/update")
    public String handleUpdate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "update-event";
        }
        eventService.update(form);
        return "index";
    }
}