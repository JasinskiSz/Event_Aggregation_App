package com.sda.eventapp.web.mvc;

import com.sda.eventapp.service.CommentService;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mapper.CommentMapper;
import com.sda.eventapp.web.mapper.EventMapper;
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
    private final CommentService commentService;

    @GetMapping("/{id}")
    public String getDetailEventView(ModelMap map, @PathVariable("id") Long id){
        map.addAttribute("event", EventMapper.toWebpage(eventService.findById(id)));
        map.addAttribute("comment", new CreateCommentForm());
        map.addAttribute("comments", CommentMapper.toWebpage(commentService.findByEventId(id)));
        return "event-detail-view";
    }


    @PostMapping("/{id}")
    public String addComment(@ModelAttribute ("comment") @Valid CreateCommentForm commentForm, @PathVariable("id") Long id, Errors errors){

        //todo fix errors validation
        if(errors.hasErrors()){
            return "event-detail-view";
        }
        CommentMapper commentMapper = new CommentMapper(eventService);
        commentService.save(commentMapper.toEntity(commentForm, id));
        return "redirect:/detail-view/" + id;
    }




}
