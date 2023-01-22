package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.startingDateTime > current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllFutureEvents();

    @Query(value = "SELECT event FROM Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE (event.startingDateTime <= ?2 and event.endingDateTime >= ?1) " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllEventByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "where event.id = ?1")
    Optional<Event> findByIdFetchOwnerFetchUsersFetchImage(Long id);
}
