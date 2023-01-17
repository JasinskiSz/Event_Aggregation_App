package com.sda.eventapp.dto;

import com.sda.eventapp.model.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class EventView {
    private long id;
    private String title;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startingDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endingDateTime;

    private Image image;
    private Set<String> usersNicknames;
    private String ownerNickname;
}
