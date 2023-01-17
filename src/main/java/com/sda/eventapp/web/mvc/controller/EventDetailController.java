package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Controller
@RequestMapping("/detail-view")
@RequiredArgsConstructor
public class EventDetailController {
    private final EventService eventService;
    private final IAuthenticationFacade authenticationFacade;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long eventId) {

        Authentication authentication = authenticationFacade.getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();
            map.addAttribute("comment", new CreateCommentForm());
            map.addAttribute("loggedUser", loggedUser);
        }


        map.addAttribute("event", eventService.findEventViewById(eventId));
        map.addAttribute("comments", eventService.findCommentViewsByEventId(eventId));

        return "event-detail-view";
    }

    @PostMapping("/{id}/add-comment")
    public String addComment(@ModelAttribute("comment") @Valid CreateCommentForm form, Errors errors, @PathVariable("id") Long eventid, RedirectAttributes ra) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        //todo trello reminder #002
        if (errors.hasErrors()) {
            ra.addFlashAttribute(
                    "commentErrors",
                    errors.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList());
            return "redirect:/detail-view/" + eventid;
        }
        eventService.saveComment(form, eventid, loggedUser);
        return "redirect:/detail-view/" + eventid;
    }

    @PostMapping("/{id}/sign-up-for-event")
    public String signupForEvent(@PathVariable("id") Long eventId) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        if (eventService.findByIdFetchOwnerFetchUsers(eventId).getOwner().getUsername().equals(loggedUser.getUsername())) {
            throw new ResponseStatusException(FORBIDDEN, "ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT");
        }
        if (eventService.findByIdFetchOwnerFetchUsers(eventId).getStartingDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED");
        }
        if (eventService.findByIdFetchOwnerFetchUsers(eventId).getUsers().contains(loggedUser)) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGNUP FOR AN EVENT IF ALREADY ASSIGNED");
        }
        eventService.signUpForEvent(loggedUser, eventId);

        return "redirect:/detail-view/" + eventId;
    }

    @PostMapping("/{id}/sign-out-from-event")
    public String signOutFromEvent(@PathVariable("id") Long eventId) {
        User loggedUser = (User) authenticationFacade.getAuthentication().getPrincipal();

        if (eventService.findByIdFetchOwnerFetchUsers(eventId).getOwner().getUsername().equals(loggedUser.getUsername())) {
            throw new ResponseStatusException(FORBIDDEN, "ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT");
        }
        if (eventService.findByIdFetchOwnerFetchUsers(eventId).getStartingDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED");
        }
        if (!eventService.findByIdFetchOwnerFetchUsers(eventId).getUsers().contains(loggedUser)) {
            throw new ResponseStatusException(BAD_REQUEST, "ACCESS DENIED - CANNOT SIGNUP OUT FROM AN EVENT IF HAS NOT ASSIGNED");
        }

        eventService.signOutFromEvent(loggedUser, eventId);

        return "redirect:/detail-view/" + eventId;
    }
}