package com.sda.eventapp.web;


import com.sda.eventapp.service.EventRepositoryService;
import com.sda.eventapp.web.dto.EventWithBasicData;
import com.sda.eventapp.web.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class EventController {
    private final EventRepositoryService eventRepositoryService;

    @GetMapping
    public String getAllEvents(ModelMap map, @Param("title") String title){

        List<EventWithBasicData> events = EventMapper.toWebpage(eventRepositoryService.findAllAfterCurrentDateTime());

        map.addAttribute("title", title);
        if(title != null){
            map.addAttribute("events", EventMapper.toWebpage(eventRepositoryService.findAllByTitle(title)));
        }
        else {
            map.addAttribute("events", events);
        }
        return "homepage";
    }

    //get mapping
    //return redirect /search>
    /*@GetMapping("/search")
    public String getAllEventsByTitle(ModelMap map, @RequestParam String title){
        List<EventWithBasicData> eventsByTitle = EventMapper.toWebpage(eventRepositoryService.findAllByTitle(title));
        map.addAttribute("eventsByTitle", eventsByTitle);
        return "homepage";
    }*/
}
