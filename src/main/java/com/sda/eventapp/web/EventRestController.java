package com.sda.eventapp.web;


import com.sda.eventapp.service.EventRepositoryService;
import com.sda.eventapp.web.dto.EventDTO;
import com.sda.eventapp.web.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class EventRestController {
    private final EventRepositoryService eventRepositoryService;

    @GetMapping
    public String getAllEvents(ModelMap map){
        List<EventDTO> eventsDTO = EventMapper.toWebpage(eventRepositoryService.findAll());
        map.addAttribute("eventsDTO", eventsDTO);
        return "homepage";
    }
}
