package com.sda.eventapp.web.mapper;

import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.User;
import com.sda.eventapp.web.dto.CommentWithBasicData;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;

import java.time.LocalDateTime;
import java.util.List;


//todo: bind with User
public class CommentMapper {


    public static Comment toEntity(CreateCommentForm form){
        return Comment.builder()
                .text(form.getText())
                .writingDate(LocalDateTime.now())
                .user(User.builder()
                        .username("ania")
                        .build())
                .build();
    }

    public static List<CommentWithBasicData> toWebpage(List<Comment> comments){
        return comments.stream().map(comment -> CommentWithBasicData.builder()
                .text(comment.getText())
                .writingDate(comment.getWritingDate())
                .build()).toList();

    }
}
