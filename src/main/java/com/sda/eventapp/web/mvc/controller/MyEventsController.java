package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.filters.DateType;
import com.sda.eventapp.filters.EventFilters;
import com.sda.eventapp.filters.ParticipationType;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping({"/my-events"})
public class MyEventsController {
    private final EventService eventService;

    private final EventFilters eventFilters = EventFilters.builder()
            .participationType(ParticipationType.OWNED_EVENTS.getName())
            .dateType(DateType.FUTURE.getName())
            .build();

    @GetMapping()
    public String getMyEventView(ModelMap map,
                                 @AuthenticationPrincipal User user,
                                 @Param("participationType") String participationType,
                                 @Param("dateType") String dateType) {
        if (Arrays.stream(ParticipationType.values())
                .map(ParticipationType::getName)
                .anyMatch(pt -> pt.equals(participationType))) {
            eventFilters.setParticipationType(participationType);
        }
        if (Arrays.stream(DateType.values())
                .map(DateType::getName)
                .anyMatch(pt -> pt.equals(dateType))) {
            eventFilters.setDateType(dateType);
        }

        map.addAttribute("loggedUser", user);
        map.addAttribute("participationTypes", ParticipationType.values());
        map.addAttribute("dateTypes", DateType.values());
        map.addAttribute("boundEvents",
                eventService.findAllEventViews(
                        user.getId(),
                        eventFilters.getParticipationType(),
                        eventFilters.getDateType()
                ));
        map.addAttribute("eventFilters", eventFilters);

        return "my-events-view";
    }
}
