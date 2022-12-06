package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllByTitle(String title);
}
