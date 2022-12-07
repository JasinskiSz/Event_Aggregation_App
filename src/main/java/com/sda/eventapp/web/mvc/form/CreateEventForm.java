package com.sda.eventapp.web.mvc.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class CreateEventForm {
    private Long id;
    @NotBlank(message = "Field title is required.")
    private String title;
    private LocalDateTime startEventDate;
    private LocalDateTime endEventDate;
    @Size(min = 20, message = "Description must be at least 20 characters long.")
    private String description;
}