package com.sda.eventapp.web.mapper;

import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.dto.CommentWithBasicData;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


//todo: bind with User
@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final EventService eventService;

    //todo: needed to change toEntity method to non static because I need to inject beans
    public Comment toEntity(CreateCommentForm form, long eventId){
        return Comment.builder()
                .text(form.getText())
                .writingDate(LocalDateTime.now())
                .user(User.builder()
                        .username("ania")
                        .build())
                .event(eventService.findById(eventId))
                .build();
    }

    public static List<CommentWithBasicData> toWebpage(List<Comment> comments){
        return comments.stream().map(comment -> CommentWithBasicData.builder()
                .text(comment.getText())
                .writingDate(comment.getWritingDate())
                .build()).toList();

    }
}
