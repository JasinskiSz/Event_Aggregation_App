package com.sda.eventapp.web.mvc.form;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
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
@EventPastTime(message = "Start date cannot be before today")
@MaxEventTime(message = "The maximum duration of the event is 2 weeks")
@DifferentDates(message = "End date must be after start date")
public class EventForm {
    private Long id;
    @NotBlank(message = "Field title is required.")
    private String title;
    private LocalDateTime startingDateTime;
    private LocalDateTime endingDateTime;
    @Size(min = 20, message = "Description must be at least 20 characters long.")
    private String description;
    private Image image;
    private User owner;
}