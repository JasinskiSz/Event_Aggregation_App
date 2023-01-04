package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.filters.DateType;
import com.sda.eventapp.filters.EventFilters;
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

    private final EventFilters eventFilters = EventFilters.builder()
            .participationType(ParticipationType.OWNED_EVENTS.getName())
            .dateType(DateType.FUTURE.getName())
            .build();

    @GetMapping()
    public String getMyEventView(ModelMap map,
                                 @Param("participationType") String participationType,
                                 @Param("dateType") String dateType) {

        if (participationType != null && dateType != null) {
            eventFilters.setParticipationType(participationType);
            eventFilters.setDateType(dateType);
        }

        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        map.addAttribute("loggedUser", loggedUser);
        map.addAttribute("participationTypes", ParticipationType.values());
        map.addAttribute("ownedEventsType", ParticipationType.OWNED_EVENTS.getName());
        map.addAttribute("attendedEventsType", ParticipationType.ATTENDED_EVENTS.getName());
        map.addAttribute("allEventsType", ParticipationType.ALL_EVENTS.getName());
        map.addAttribute("dateTypes", DateType.values());
        map.addAttribute("boundEvents",
                eventService.findAllEventViews(
                        loggedUser.getId(),
                        eventFilters.getParticipationType(),
                        eventFilters.getDateType()
                ));
        map.addAttribute("eventFilters", eventFilters);

        return "my-events-view";
    }
}
