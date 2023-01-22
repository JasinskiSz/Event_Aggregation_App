package com.sda.eventapp.mapper;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public List<CommentView> toCommentViewList(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentView.builder()
                        .text(comment.getText())
                        .writingDate(comment.getWritingDate())
                        .userNickname(comment.getUser().getUsername())
                        .build())
                .toList();
    }

    public Comment toComment(CreateCommentForm form, Event event, User user) {
        return Comment.builder()
                .text(form.getText())
                .writingDate(LocalDateTime.now())
                .user(user)
                .event(event)
                .build();
    }
}
