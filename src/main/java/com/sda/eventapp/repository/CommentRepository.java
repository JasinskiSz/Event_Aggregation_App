package com.sda.eventapp.repository;

import com.sda.eventapp.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdOrderByWritingDateDesc(long eventId);
}
