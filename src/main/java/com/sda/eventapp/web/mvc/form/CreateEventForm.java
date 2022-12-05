package com.sda.eventapp.web.mvc.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@ToString
@Getter
@Setter
public class CreateEventForm {
    private Long id;
    @NotBlank(message = "Field title is required.")
    private String title;
    private Date startEventDate;
    private Date endEventDate;
    @Size(min = 20, message = "Description must be min 20 characters long.")
    private String description;
}
