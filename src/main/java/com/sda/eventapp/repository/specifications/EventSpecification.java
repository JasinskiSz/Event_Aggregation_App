package com.sda.eventapp.repository.specifications;

import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
import jakarta.persistence.criteria.JoinType;
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
                criteriaBuilder.lessThan(root.get("endingDateTime"), LocalDateTime.now());
    }

    public static Specification<Event> isOngoing() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.get("startingDateTime"), LocalDateTime.now()),
                        criteriaBuilder.greaterThan(root.get("endingDateTime"), LocalDateTime.now())
                );
    }

    public static Specification<Event> titleContains(String title) {
        if(title == null || title.isBlank()){
            return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + "" + "%");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Event> orderByEventStartingDate() {
        return (root, query, criteriaBuilder) ->
                query.orderBy(criteriaBuilder.asc(root.get("startingDateTime"))).getRestriction();
    }

    public static Specification<Event> fetchAllEntities() {
        return (root, query, criteriaBuilder) -> {
            root.fetch("users", JoinType.LEFT);
            root.fetch("image", JoinType.LEFT);
            root.fetch("owner", JoinType.LEFT);
            return query.getRestriction();
        };
    }
}
