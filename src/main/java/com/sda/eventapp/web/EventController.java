package com.sda.eventapp.web;

import com.sda.eventapp.service.EventRepositoryService;
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
public class EventController {
    private final EventRepositoryService eventRepositoryService;

    @GetMapping
    public String getAllEvents(ModelMap map, @Param("title") String title, @Param("futureEventsFilter") boolean futureEventsFilter, @Param("ongoingEventsFilter") boolean ongoingEventsFilter, @Param("pastEventsFilter") boolean pastEventsFilter) {
        map.addAttribute("title", title);
        if (title != null && !title.equals("")) {
            map.addAttribute("events", EventMapper.toWebpage(eventRepositoryService.findAllByTitleWithFilters(title, futureEventsFilter, ongoingEventsFilter, pastEventsFilter)));
        } else {
            map.addAttribute("events", EventMapper.toWebpage(eventRepositoryService.findAllWithFilters(futureEventsFilter, ongoingEventsFilter, pastEventsFilter)));
        }
        return "homepage";
    }
}
