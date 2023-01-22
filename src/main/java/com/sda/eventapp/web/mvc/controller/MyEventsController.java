package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.filters.DateType;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping({"/my-events"})
public class MyEventsController {
    private final EventService eventService;
    private final FiltersService filtersService;

    @GetMapping()
    public String getMyEventView(ModelMap map,
                                 @AuthenticationPrincipal User user,
                                 @Param("participationType") String participationType,
                                 @Param("dateType") String dateType) {
        map.addAttribute("loggedUser", user);
        map.addAttribute("participationTypes", ParticipationType.values());
        map.addAttribute("dateTypes", DateType.values());
        map.addAttribute(
                "boundEvents", eventService.findAllEventViews(user.getId(), participationType, dateType)
        );
        map.addAttribute("eventFilters", filtersService.getEventFilters());

        return "my-events-view";
    }
}
