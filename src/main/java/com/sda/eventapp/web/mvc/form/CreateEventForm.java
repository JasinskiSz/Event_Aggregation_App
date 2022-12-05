package com.sda.eventapp.web.mvc.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class CreateEventForm {
    private Long id;
    @NotBlank(message = "Field title is required.")
    private String title;
    // TODO: Need to change type Data to LocalDataTime. LocalDataTime now doesn't work with Create Event Form, because of calender field.
    private Date startEventDate;
    private Date endEventDate;
    @Size(min = 20, message = "Description must be min 20 characters long.")
    private String description;
}