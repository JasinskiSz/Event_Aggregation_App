package com.sda.eventapp.web.mvc;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import com.sda.eventapp.web.mvc.mappers.EventMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping({"/event"})
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/create")
    public String createEvent(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping("/create")
    public String createEvenyByPost(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "create-event";
        }
        eventService.save(EventMapper.toEntity(form));
        return "index";
    }

    @GetMapping("/update/{id}")
    public String updateEvent(ModelMap model, @PathVariable Long id) {
        model.addAttribute("event", new CreateEventForm());
        Event foundEvent = eventService.findById(id);
        model.addAttribute("event", foundEvent);
        return "update-event";
    }

    @PostMapping("/update")
    public String updateEventByPost(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "update-event";
        }
        eventService.updateByModify(EventMapper.toEntity(form), form);
        return "index";
    }
}
