package com.sda.eventapp.web.rest.controller;

import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventRestController {
    private final EventService eventService;

    @GetMapping("/all")
    public List<EventView> getEventViews() {
        return eventService.findAllEventViews();
    }

    @GetMapping("/date")
    public List<EventView> getEventViewsByDateRange(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return eventService.findEventViewsByDateRange(start, end);
    }
}