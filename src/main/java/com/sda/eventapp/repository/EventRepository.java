package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllFutureEvents();

    @Query(value = "SELECT event from Event event WHERE current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime ORDER BY event.startingDateTime")
    List<Event> findAllOngoingEvents();

    @Query(value = "SELECT event from Event event WHERE event.endingDateTime < current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllPastEvents();

    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp or event.endingDateTime < current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllFutureAndPastEvents();

    @Query(value = "SELECT event from Event event WHERE event.endingDateTime < current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndPastEvents();

    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndFutureEvents();

    @Query(value = "SELECT event from Event event WHERE event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllFutureEventsByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllOngoingEventsByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE event.endingDateTime < current_timestamp and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE (event.startingDateTime > current_timestamp or event.endingDateTime < current_timestamp) and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllFutureAndPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE (event.endingDateTime < current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime) and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event WHERE (event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime) and event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndFutureEventsByTitle(String title);

    @Query(value = "SELECT event FROM Event event WHERE  (event.startingDateTime <= ?2 and event.endingDateTime >= ?1)")
    List<Event> findAllEventByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Event> findAllByOwner(User owner);

    List<Event> findAllByUsers_Username(String nickname);
}
