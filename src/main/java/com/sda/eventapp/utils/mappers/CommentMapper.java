package com.sda.eventapp.utils.mappers;

import com.sda.eventapp.dto.form.CreateCommentForm;
import com.sda.eventapp.dto.views.CommentView;
import com.sda.eventapp.model.entities.Comment;
import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
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
