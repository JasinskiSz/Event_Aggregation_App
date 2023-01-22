package com.sda.eventapp.dto.form.validation.validator;

import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.dto.form.validation.constraint.UniqueUsername;
import com.sda.eventapp.service.UserService;
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
        return !userService.existsByUsername(form.getUsername());
    }
}
