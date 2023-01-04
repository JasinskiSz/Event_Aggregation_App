package com.sda.eventapp.dto;

import com.sda.eventapp.model.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventView {
    private long id;
    private String title;
    private String description;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
    private Image image;
}
