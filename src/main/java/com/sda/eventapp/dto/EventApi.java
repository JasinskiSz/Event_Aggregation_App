package com.sda.eventapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public class EventApi {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
    private Set<String> attenders;
    private String ownerNickname;
}
