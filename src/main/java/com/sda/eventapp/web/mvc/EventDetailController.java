package com.sda.eventapp.web.mvc;

import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mapper.EventMapper;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detail-view")
@RequiredArgsConstructor
public class EventDetailController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long id){
        map.addAttribute("event", EventMapper.toWebpage(eventService.findById(id)));
        map.addAttribute("comment", new CreateCommentForm());
        return "event-detail-view";
    }


    @PostMapping("/{id}")
    public String addComment(@ModelAttribute CreateCommentForm commentForm,  @PathVariable("id") Long id){

        return "redirect:/detail-view/" + id;
    }




}
