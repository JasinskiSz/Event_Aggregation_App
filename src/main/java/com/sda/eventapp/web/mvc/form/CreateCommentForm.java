package com.sda.eventapp.web.mvc.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCommentForm {
    @Size(max = 500, message = "Comment cannot have more than 500 characters")
    @NotBlank(message = "Comment cannot be empty")
    private String text;
}
