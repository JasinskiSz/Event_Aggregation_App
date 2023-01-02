package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.filters.DateType;
import com.sda.eventapp.filters.ParticipationType;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
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
    public String getMyEventView(ModelMap map,
                                 @Param("participationType") String participationType,
                                 @Param("dateType") String dateType) {
        System.out.println(participationType + " " + dateType);

        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();
        map.addAttribute("loggedUser", loggedUser);
        map.addAttribute("participationTypes", ParticipationType.values());
        map.addAttribute("dateTypes", DateType.values());


        map.addAttribute("ownedEvents", eventService.findOwnedEvents(loggedUser));
        map.addAttribute("attendingEvents", eventService.findAttendingEvents(loggedUser.getUsername()));


        return "my-events-view";
    }

    /*@GetMapping
    public String getAllEventsView(ModelMap map,
                                   @Param("title") String title,
                                   @Param("futureEventsFilter") boolean futureEventsFilter,
                                   @Param("ongoingEventsFilter") boolean ongoingEventsFilter,
                                   @Param("pastEventsFilter") boolean pastEventsFilter) {
        map.addAttribute("title", title);
        map.addAttribute("events", eventService.findAllEventViews(title, futureEventsFilter,
                ongoingEventsFilter, pastEventsFilter));

        return "homepage";
    }*/
}
