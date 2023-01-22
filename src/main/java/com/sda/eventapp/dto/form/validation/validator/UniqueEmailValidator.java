package com.sda.eventapp.dto.form.validation.validator;

import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.dto.form.validation.constraint.UniqueEmail;
import com.sda.eventapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, CreateUserForm> {
    private final UserService userService;

    @Override
    public void initialize(UniqueEmail constraint) {
    }

    @Override
    public boolean isValid(CreateUserForm form, ConstraintValidatorContext context) {
        return !userService.existsByEmail(form.getEmail());
    }
}
