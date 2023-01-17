package com.sda.eventapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
public class EventApi {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
    private String imageUrl; // If not on localhost this would be real imageUrl
    private Set<String> attenders;
    private String ownerNickname;
}
