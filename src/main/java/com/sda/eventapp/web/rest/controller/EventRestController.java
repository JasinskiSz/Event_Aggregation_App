package com.sda.eventapp.web.rest.controller;

import com.sda.eventapp.web.dto.EventWithBasicData;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mapper.EventMapper;
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
@RequestMapping("event")
@RequiredArgsConstructor
public class EventRestController {
    private final EventService eventService;

    @GetMapping("all")
    public List<EventWithBasicData> getEvents() {
        return EventMapper.toWebpage(eventService.findAllEvents());
    }

    @GetMapping("date")
    public List<EventWithBasicData> getEventsByDateRange(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return EventMapper.toWebpage(eventService.findEventByDateRange(start, end));
    }
}