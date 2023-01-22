package com.sda.eventapp.filters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventFilters {
    private ParticipationType participationType;
    private DateType dateType;
}
