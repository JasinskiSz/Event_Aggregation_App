package com.sda.eventapp.dto.form;

import com.sda.eventapp.dto.form.validation.constraint.EqualPasswords;
import com.sda.eventapp.dto.form.validation.constraint.UniqueEmail;
import com.sda.eventapp.dto.form.validation.constraint.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@UniqueEmail(message = "That email is already taken")
@UniqueUsername(message = "That nickname is already taken")
@EqualPasswords(message = "Passwords do not match")
public class CreateUserForm {
    @NotBlank(message = "Field nickname is required")
    @Size(max = 50, message = "Nickname must be shorter than 50 characters")
    private String username;
    @Size(min = 8, max = 30, message = "Password must be 8-30 characters long")
    private String password;
    @NotBlank(message = "Field email is required")
    @Email(message = "Invalid email")
    private String email;
    private String confirmPassword;
}