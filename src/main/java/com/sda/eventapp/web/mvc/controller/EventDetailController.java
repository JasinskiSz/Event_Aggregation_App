package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detail-view")
@RequiredArgsConstructor
public class EventDetailController {
    private final EventService eventService;
    private final IAuthenticationFacade authenticationFacade;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long eventId) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        map.addAttribute("loggedUser", loggedUser);
        map.addAttribute("event", eventService.findEventViewById(eventId));
        map.addAttribute("comment", new CreateCommentForm());
        map.addAttribute("comments", eventService.findCommentViewsByEventId(eventId));

        return "event-detail-view";
    }

    @PostMapping("/{id}")
    public String addComment(@ModelAttribute("comment") @Valid CreateCommentForm form, Errors errors, @PathVariable("id") Long eventid) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        //todo trello reminder #002
        if (errors.hasErrors()) {
            return "redirect:/detail-view/" + eventid;
        }
        eventService.saveComment(form, eventid, loggedUser);
        return "redirect:/detail-view/" + eventid;
    }

    @PostMapping("/{id}/sign-up-for-event")
    public String signupForEvent(@PathVariable("id") Long eventId) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        eventService.signUpForEvent(loggedUser, eventId);

        return "redirect:/detail-view/" + eventId;
    }

    @PostMapping("/{id}/sign-out-from-event")
    public String signOutFromEvent(@PathVariable("id") Long eventId) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        eventService.signOutFromEvent(loggedUser, eventId);

        return "redirect:/detail-view/" + eventId;
    }
}