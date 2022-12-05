package com.sda.eventapp.service;

import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryService implements CommandLineRunner {

    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) throws Exception {
        /*commentRepository.save(Comment.builder()
                .text("This is the test comment text")
                .build());*/


    }
}
