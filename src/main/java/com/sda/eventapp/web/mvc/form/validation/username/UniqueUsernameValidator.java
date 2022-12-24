package com.sda.eventapp.web.mvc.form.validation.username;

import com.sda.eventapp.service.UserService;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, CreateUserForm> {
    private final UserService userService;

    @Override
    public void initialize(UniqueUsername constraint) {
    }

    @Override
    public boolean isValid(CreateUserForm form, ConstraintValidatorContext context) {
        return !userService.existUsernameinDatabase(form.getUsername());
    }
}
