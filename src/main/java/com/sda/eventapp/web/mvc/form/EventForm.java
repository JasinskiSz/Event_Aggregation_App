package com.sda.eventapp.web.mvc.form;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.web.mvc.form.validation.constraint.DifferentDates;
import com.sda.eventapp.web.mvc.form.validation.constraint.EventPastTime;
import com.sda.eventapp.web.mvc.form.validation.constraint.MaxEventTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EventPastTime(message = "Starting date cannot be a past date")
@MaxEventTime(message = "The maximum duration of the event is 2 weeks")
@DifferentDates(message = "Starting date must be before ending date of event")
public class EventForm {
    private Long id;
    @NotBlank(message = "Field title is required.")
    private String title;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
    @Size(min = 20, message = "Description must be at least 20 characters long.")
    private String description;
    private Image image;
}