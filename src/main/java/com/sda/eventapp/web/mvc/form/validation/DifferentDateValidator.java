package com.sda.eventapp.web.mvc.form.validation;


import com.sda.eventapp.web.mvc.form.CreateEventForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentDateValidator implements ConstraintValidator<DifferentDate, CreateEventForm> {

    @Override
    public void initialize(DifferentDate constraint) {
    }

    @Override
    public boolean isValid(CreateEventForm form, ConstraintValidatorContext context) {
        return form.getStartingDateTime().isBefore(form.getEndingDateTime());
    }
}