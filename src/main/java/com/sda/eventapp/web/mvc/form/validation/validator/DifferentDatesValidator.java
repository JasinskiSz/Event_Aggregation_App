package com.sda.eventapp.web.mvc.form.validation.validator;

import com.sda.eventapp.web.mvc.form.CreateEventForm;
import com.sda.eventapp.web.mvc.form.validation.constraint.DifferentDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentDatesValidator implements ConstraintValidator<DifferentDates, CreateEventForm> {

    @Override
    public void initialize(DifferentDates constraint) {
    }

    @Override
    public boolean isValid(CreateEventForm form, ConstraintValidatorContext context) {
        return form.getStartingDateTime()
                .isBefore(form.getEndingDateTime());
    }
}