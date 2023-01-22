package com.sda.eventapp.repository.specification;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EventSpecification {
    public static Specification<Event> isOwnedBy(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), id);
    }

    public static Specification<Event> isAttendedBy(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(User.builder().id(id).build(), root.get("users"));
    }

    public static Specification<Event> isFuture() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("startingDateTime"), LocalDateTime.now());
    }

    public static Specification<Event> isPast() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("startingDateTime"), LocalDateTime.now());
    }

    public static Specification<Event> isOngoing() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.get("startingDateTime"), LocalDateTime.now()),
                        criteriaBuilder.greaterThan(root.get("endingDateTime"), LocalDateTime.now())
                );
    }
}
