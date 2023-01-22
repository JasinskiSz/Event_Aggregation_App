package com.sda.eventapp.dto.form.validation.validator;

import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.dto.form.validation.constraint.EqualPasswords;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EqualPasswordsValidator implements ConstraintValidator<EqualPasswords, CreateUserForm> {

    @Override
    public void initialize(EqualPasswords constraint) {
    }

    @Override
    public boolean isValid(CreateUserForm form, ConstraintValidatorContext context) {
        return form.getPassword()
                .equals(form.getConfirmPassword());
    }
}
