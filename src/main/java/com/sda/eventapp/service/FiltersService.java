package com.sda.eventapp.service;

import com.sda.eventapp.entities.Event;
import com.sda.eventapp.filters.DateType;
import com.sda.eventapp.filters.ParticipationType;
import com.sda.eventapp.model.EventFilters;
import com.sda.eventapp.repository.specification.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FiltersService {
    private final EventFilters eventFilters = EventFilters.builder()
            .participationType(ParticipationType.OWNED) // default value to be displayed
            .dateType(DateType.FUTURE) // default value to be displayed
            .build();

    public Specification<Event> prepareSpecification(Long userId, String participationType, String dateType) {
        // default values for filters
        ParticipationType participationTypeEnum = ParticipationType.OWNED;
        DateType dateTypeEnum = DateType.FUTURE;

        // fix possible name error when enums name is with spaces
        String participationTypeFixed = "";
        String dateTypeFixed = "";
        if (participationType != null) {
            participationTypeFixed = participationType.replaceAll(" ", "_");
        }
        if (dateType != null) {
            dateTypeFixed = dateType.replaceAll(" ", "_");
        }

        // if filter is valid, set it to variable
        if (EnumUtils.isValidEnumIgnoreCase(ParticipationType.class, participationTypeFixed)) {
            participationTypeEnum = EnumUtils.getEnumIgnoreCase(ParticipationType.class, participationTypeFixed);
        }

        // if filter is valid, set it to variable
        if (EnumUtils.isValidEnumIgnoreCase(DateType.class, dateTypeFixed)) {
            dateTypeEnum = EnumUtils.getEnumIgnoreCase(DateType.class, dateTypeFixed);
        }

        eventFilters.setParticipationType(participationTypeEnum);
        eventFilters.setDateType(dateTypeEnum);

        Specification<Event> specification = Specification.where(null);

        if (eventFilters.getParticipationType() == ParticipationType.OWNED) {
            specification = specification.and(EventSpecification.isOwnedBy(userId));
        } else if (eventFilters.getParticipationType() == ParticipationType.ATTENDED) {
            specification = specification.and(EventSpecification.isAttendedBy(userId));
        } else if (eventFilters.getParticipationType() == ParticipationType.ALL) {
            specification = specification.and(EventSpecification.isOwnedBy(userId).or(EventSpecification.isAttendedBy(userId)));
        }

        if (eventFilters.getDateType() == DateType.FUTURE) {
            specification = specification.and(EventSpecification.isFuture());
        } else if (eventFilters.getDateType() == DateType.FUTURE_AND_ONGOING) {
            specification = specification.and(EventSpecification.isOngoing());
        } else if (eventFilters.getDateType() == DateType.PAST) {
            specification = specification.and(EventSpecification.isPast());
        }

        return specification.and(EventSpecification.fetchAllEntities()).and(EventSpecification.orderByEventStartingDate());
    }

    public Specification<Event> prepareSpecification(String title, boolean future, boolean ongoing, boolean past) {
        Specification<Event> specification = Specification.where(null);

        if (title != null && !title.isBlank()) {
            specification = specification.or(EventSpecification.titleContains(title));
        }

        if (future) {
            specification = specification.or(EventSpecification.isFuture());
        }

        if (ongoing) {
            specification = specification.or(EventSpecification.isOngoing());
        }

        if (past) {
            specification = specification.or(EventSpecification.isPast());
        }

        return specification.and(EventSpecification.fetchAllEntities()).and(EventSpecification.orderByEventStartingDate());
    }

    public EventFilters getEventFilters() {
        return eventFilters;
    }
}
