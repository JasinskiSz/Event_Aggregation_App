package com.sda.eventapp.model;

import com.sda.eventapp.types.DateType;
import com.sda.eventapp.types.ParticipationType;
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
