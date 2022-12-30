package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.service.UserService;
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
    private final UserService userService;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long id) {
        map.addAttribute("event", eventService.findEventViewById(id));
        map.addAttribute("comment", new CreateCommentForm());
        map.addAttribute("comments", eventService.findCommentViewsByEventId(id));
        map.addAttribute("users", userService.eventUserList(id));

        return "event-detail-view";
    }


    @PostMapping("/{id}")
    public String addComment(@ModelAttribute("comment") @Valid CreateCommentForm form, Errors errors, @PathVariable("id") Long eventID) {

        //todo trello reminder #002
        if (errors.hasErrors()) {
            return "redirect:/detail-view/" + eventID;
        }
        eventService.saveComment(form, eventID);
        return "redirect:/detail-view/" + eventID;
    }
}
