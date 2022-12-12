package com.sda.eventapp.service;

import com.sda.eventapp.model.Comment;
import com.sda.eventapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment save(Comment comment){
        return commentRepository.save(comment);
    }


    public List<Comment> findAll() {
        return StreamSupport.stream(commentRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<Comment> findByEventId(Long id) {
        return commentRepository.findAllByEvent_IdOrderByWritingDateDesc(id);
    }
}
