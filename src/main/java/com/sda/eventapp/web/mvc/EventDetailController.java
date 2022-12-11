package com.sda.eventapp.web.mvc;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/detail-view")
@RequiredArgsConstructor
public class EventDetailController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long id){
        map.addAttribute("event", EventMapper.toWebpage(eventService.findById(id)));
        return "event-detail-view";
    }
}
