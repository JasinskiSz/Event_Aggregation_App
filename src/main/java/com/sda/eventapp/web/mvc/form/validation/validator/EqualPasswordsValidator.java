package com.sda.eventapp.web.mvc.form.validation.validator;

import com.sda.eventapp.web.mvc.form.CreateUserForm;
import com.sda.eventapp.web.mvc.form.validation.constraint.EqualPasswords;
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
