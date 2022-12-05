package com.sda.eventapp.web;


import com.sda.eventapp.model.Event;
import com.sda.eventapp.service.EventRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class EventRestController {
    private final EventRepositoryService eventRepositoryService;

    @GetMapping
    public List<Event> getAllEvents(){
        return eventRepositoryService.findAll();
    }
}
