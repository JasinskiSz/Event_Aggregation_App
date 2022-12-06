package com.sda.eventapp.web.mvc;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import com.sda.eventapp.web.mvc.mappers.EventMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping({"/create/event"})
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public String createEvent(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping
    public String createEventByPost(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "create-event";
        }
        eventService.save(EventMapper.toEntity(form));
        return "index";
    }
}