package com.sda.eventapp.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventWithBasicData {

    private String title;
    private String description;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
}
