package com.sda.eventapp.web.mvc.form;

import com.sda.eventapp.web.mvc.form.validation.constraint.EqualPasswords;
import com.sda.eventapp.web.mvc.form.validation.constraint.UniqueEmail;
import com.sda.eventapp.web.mvc.form.validation.constraint.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@UniqueEmail(message = "That email is already taken. Try again")
@UniqueUsername(message = "That nickname is already taken. Try again")
@EqualPasswords(message = "Passwords do not match")
public class CreateUserForm {
    @NotBlank(message = "Field username is required")
    @Size(max = 50, message = "Username must be shorter than 50 characters.")
    private String username;
    @NotBlank(message = "Field password is required")
    @Size(min = 8, max = 30, message = "Password must be 8-30 characters long")
    private String password;
    @NotBlank(message = "Field email is required")
    @Email(message = "Invalid email")
    private String email;
    private String confirmPassword;
}