package com.sda.eventapp.service;

import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.mapper.CommentMapper;
import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.repository.CommentRepository;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
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

    public Comment save(CreateCommentForm form, Event event) {
        return repository.save(mapper.toEntity(form, event));
    }

    public List<CommentView> findCommentViewsByEventId(Long id) {
        return mapper.toWebpage(repository.findAllByEvent_IdOrderByWritingDateDesc(id));
    }
}
