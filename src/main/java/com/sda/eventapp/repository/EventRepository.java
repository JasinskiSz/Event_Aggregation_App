package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.startingDateTime > current_timestamp ORDER BY event.startingDateTime")
    List<Event> findAllFutureEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.endingDateTime < current_timestamp " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllPastEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.startingDateTime > current_timestamp or event.endingDateTime < current_timestamp " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllFutureAndPastEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.endingDateTime < current_timestamp or current_timestamp >= event.startingDateTime " +
            "and current_timestamp <= event.endingDateTime " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndPastEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime " +
            "and current_timestamp <= event.endingDateTime " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndFutureEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllEvents();

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.title like %?1% ORDER BY event.startingDateTime")
    List<Event> findAllByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.startingDateTime > current_timestamp and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllFutureEventsByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE current_timestamp >= event.startingDateTime " +
            "and current_timestamp <= event.endingDateTime and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingEventsByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE event.endingDateTime < current_timestamp and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE (event.startingDateTime > current_timestamp or event.endingDateTime < current_timestamp) " +
            "and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllFutureAndPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE (event.endingDateTime < current_timestamp or current_timestamp >= event.startingDateTime " +
            "and current_timestamp <= event.endingDateTime) and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndPastEventsByTitle(String title);

    @Query(value = "SELECT event from Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE (event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime " +
            "and current_timestamp <= event.endingDateTime) and event.title like %?1% " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllOngoingAndFutureEventsByTitle(String title);

    @Query(value = "SELECT event FROM Event event " +
            "left join fetch event.owner owner left join fetch event.users users left join fetch event.image image " +
            "WHERE (event.startingDateTime <= ?2 and event.endingDateTime >= ?1) " +
            "ORDER BY event.startingDateTime")
    List<Event> findAllEventByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT event from Event event WHERE event.startingDateTime > current_timestamp and event.owner.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findOwnedFutureEventsByOwner_Id(Long id);

    @Query(value = "SELECT event from Event event WHERE (event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime) and event.owner.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findOwnedFutureAndOngoingEventsByOwner_Id(Long id);

    @Query(value = "SELECT event from Event event WHERE event.endingDateTime < current_timestamp and event.owner.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findOwnedPastEventsByOwner_Id(Long id);

    List<Event> findOwnedAllEventsByOwner_IdOrderByStartingDateTime(Long id);

    @Query(value = "SELECT event from Event event left join event.users users WHERE event.startingDateTime > current_timestamp and users.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findAttendedFutureEventsById(Long id);

    @Query(value = "SELECT event from Event event left join event.users users  WHERE (event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime) and users.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findAttendedFutureAndOngoingEventsById(Long id);

    @Query(value = "SELECT event from Event event left join event.users users WHERE event.endingDateTime < current_timestamp and users.id = ?1 ORDER BY event.startingDateTime")
    List<Event> findAttendedPastEventsById(Long id);

    List<Event> findAttendedAllEventsByUsers_IdOrderByStartingDateTime(Long id);

    @Query(value = "SELECT event from Event event left join event.users users " +
            "WHERE event.startingDateTime > current_timestamp and (event.owner.id = ?1 or users.id = ?1) " +
            "ORDER BY event.startingDateTime")
    List<Event> findOwnedAndAttendedFutureEventsById(Long id);

    @Query(value = "SELECT event from Event event left join event.users users WHERE (event.startingDateTime > current_timestamp or current_timestamp >= event.startingDateTime and current_timestamp <= event.endingDateTime) and (event.owner.id = ?1 or users.id = ?1) ORDER BY event.startingDateTime")
    List<Event> findOwnedAndAttendedFutureAndOngoingEventsById(Long id);

    @Query(value = "SELECT event from Event event left join event.users users WHERE event.endingDateTime < current_timestamp and (event.owner.id = ?1 or users.id = ?1) ORDER BY event.startingDateTime")
    List<Event> findOwnedAndAttendedPastEventsById(Long id);

    @Query(value = "SELECT event from Event event left join event.users users WHERE (event.owner.id = ?1 or users.id = ?1) ORDER BY event.startingDateTime")
    List<Event> findOwnedAndAttendedAllEventsById(Long id);

    @Query(value = "SELECT event from Event event left join fetch event.owner owner left join fetch event.users users where event.id = ?1")
    Optional<Event> findByIdFetchOwnerFetchUsers(Long id);
}
