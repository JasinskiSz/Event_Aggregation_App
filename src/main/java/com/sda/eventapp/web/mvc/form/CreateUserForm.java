package com.sda.eventapp.web.mvc.form;

import com.sda.eventapp.web.mvc.form.validation.email.UniqueEmail;
import com.sda.eventapp.web.mvc.form.validation.password.EqualPasswords;
import com.sda.eventapp.web.mvc.form.validation.username.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@UniqueEmail(message = "[Email is already exists.]")
@UniqueUsername(message = "[Username is already exists.]")
@EqualPasswords(message = "[Passwords do NOT match]")

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