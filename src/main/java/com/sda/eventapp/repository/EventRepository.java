package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends CrudRepository<Event, Long> {

    @Query("SELECT event from Event event where event.title like %?1%")
    List<Event> findAllByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllAfterCurrentDateTime();
}
