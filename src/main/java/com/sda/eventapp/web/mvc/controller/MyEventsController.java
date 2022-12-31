package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping({"/my-events"})
public class MyEventsController {

    private final EventService eventService;
    private final IAuthenticationFacade authenticationFacade;

    @GetMapping()
    public String getMyEventView(ModelMap map) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();
        map.addAttribute("loggedUser", loggedUser);
        map.addAttribute("ownedEvents", eventService.findOwnedEvents(loggedUser));
        map.addAttribute("attendingEvents", eventService.findAttendingEvents(loggedUser.getUsername()));


        return "my-events-view";
    }
}
