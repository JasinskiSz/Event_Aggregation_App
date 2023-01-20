package com.sda.eventapp.repository;

import com.sda.eventapp.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByEvent_IdOrderByWritingDateDesc(long event_id);




}
