package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.sda.eventapp.utils.AuthUtils.isUserLoggedIn;

@Controller
@RequestMapping("/detail-view")
@RequiredArgsConstructor
public class EventDetailController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, Authentication authentication, @PathVariable("id") Long eventId) {
        if (isUserLoggedIn(authentication)) {
            User user = (User) authentication.getPrincipal();
            map.addAttribute("loggedUser", user);
            map.addAttribute("comment", new CreateCommentForm());
            map.addAttribute("adminRole", new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        map.addAttribute("event", eventService.findEventViewById(eventId));
        map.addAttribute("comments", eventService.findCommentViewsByEventId(eventId));

        return "event-detail-view";
    }

    @PostMapping("/{id}/add-comment")
    public String addComment(@AuthenticationPrincipal User user,
                             @ModelAttribute("comment") @Valid CreateCommentForm form,
                             Errors errors,
                             @PathVariable("id") Long eventId,
                             RedirectAttributes ra) {
        //todo trello reminder #002
        if (errors.hasErrors()) {
            ra.addFlashAttribute(
                    "commentErrors",
                    errors.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList()
            );
            return "redirect:/detail-view/" + eventId;
        }
        eventService.saveComment(form, eventId, user);
        return "redirect:/detail-view/" + eventId;
    }

    @PostMapping("/{id}/sign-up-for-event")
    public String signupForEvent(@AuthenticationPrincipal User user, @PathVariable("id") Long eventId) {
        eventService.signUpForEvent(user, eventId);
        return "redirect:/detail-view/" + eventId;
    }

    @PostMapping("/{id}/sign-out-from-event")
    public String signOutFromEvent(@AuthenticationPrincipal User user, @PathVariable("id") Long eventId) {
        eventService.signOutFromEvent(user, eventId);
        return "redirect:/detail-view/" + eventId;
    }
}