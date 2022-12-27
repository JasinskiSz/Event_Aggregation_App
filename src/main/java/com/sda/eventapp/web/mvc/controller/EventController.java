package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.service.EventService;
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
    private final EventService service;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors, @RequestParam MultipartFile img, RedirectAttributes ra) {

        if (errors.hasErrors()) {
            return "create-event";
        }
        if (img.getOriginalFilename().isBlank()) {
            return service.createEventWithoutPhoto(form);
        }
        return service.createEventWithPhoto(form, img, ra);
    }



    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String handleImageUploadError(RedirectAttributes ra) {
        System.out.println("Caught file upload error");
        ra.addFlashAttribute("uploadError", "You could not upload file bigger than " + maxFileSize);
        return "redirect:/event/create";
    }

    @GetMapping("/update/{id}")
    public String update(ModelMap model, @PathVariable Long id) {
        model.addAttribute("event", new CreateEventForm());
        model.addAttribute("event", service.findById(id));
        return "update-event";
    }

    @PostMapping("/update")
    public String handleUpdate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "update-event";
        }
        service.update(form);
        return "index";
    }
}