package com.sda.eventapp.web.mvc;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import com.sda.eventapp.web.mvc.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping({"/createEvent"})
@RequiredArgsConstructor

public class EventController {
    private final EventService eventService;


    @GetMapping
    public String createEvent(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping
    public String createEvenyByPost(@ModelAttribute("event") @Valid CreateEventForm form,
                                    Errors errors, RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            return "create-event";
        }
        eventService.save(EventMapper.toEntity(form));
        return "create-event";
    }
}
