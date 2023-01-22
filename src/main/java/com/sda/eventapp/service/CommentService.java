package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.form.CreateCommentForm;
import com.sda.eventapp.entities.Comment;
import com.sda.eventapp.entities.Event;
import com.sda.eventapp.entities.User;
import com.sda.eventapp.repository.CommentRepository;
import com.sda.eventapp.utils.mappers.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    private final CommentMapper mapper;

    public Comment save(CreateCommentForm form, Event event, User user) {
        return repository.save(mapper.toComment(form, event, user));
    }

    public List<CommentView> findCommentViewsByEventId(Long eventId) {
        return mapper.toCommentViewList(repository.findAllByEventIdOrderByWritingDateDesc(eventId));
    }
}
