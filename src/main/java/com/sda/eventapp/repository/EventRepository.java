package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT event FROM Event event WHERE  (event.startingDateTime <= ?2 and event.endingDateTime >= ?1)")
    List<Event> findAllEventByRangeDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}

