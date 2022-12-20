package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class HomepageController {
    private final EventService eventService;

    @GetMapping
    public String getAllEvents(ModelMap map,
                               @Param("title") String title,
                               @Param("futureEventsFilter") boolean futureEventsFilter,
                               @Param("ongoingEventsFilter") boolean ongoingEventsFilter,
                               @Param("pastEventsFilter") boolean pastEventsFilter) {
        map.addAttribute("title", title);
        if (title != null && !title.equals("")) {
            map.addAttribute("events", EventMapper.toWebpage(eventService.findAllByTitleWithFilters(
                    title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter)));
        } else {
            map.addAttribute("events", EventMapper.toWebpage(eventService.findAllWithFilters(
                    futureEventsFilter, ongoingEventsFilter, pastEventsFilter)));
        }
        return "homepage";
    }
}
