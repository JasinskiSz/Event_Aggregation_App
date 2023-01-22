package com.sda.eventapp.web.mvc.controllers;

import com.sda.eventapp.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/home", "/"})
@RequiredArgsConstructor
public class HomepageController {
    private final EventService eventService;

    @GetMapping
    public String getAllEventsView(ModelMap map,
                                   @Param("title") String title,
                                   @Param("futureEventsFilter") boolean futureEventsFilter,
                                   @Param("ongoingEventsFilter") boolean ongoingEventsFilter,
                                   @Param("pastEventsFilter") boolean pastEventsFilter) {
        map.addAttribute("title", title);
        map.addAttribute("events", eventService.findAllEventViews(title, futureEventsFilter,
                ongoingEventsFilter, pastEventsFilter));

        return "index";
    }
}
