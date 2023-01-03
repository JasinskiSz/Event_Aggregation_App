package com.sda.eventapp.web.mvc.form.validation.validator;

import com.sda.eventapp.web.mvc.form.EventForm;
import com.sda.eventapp.web.mvc.form.validation.constraint.DifferentDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentDatesValidator implements ConstraintValidator<DifferentDates, EventForm> {

    @Override
    public void initialize(DifferentDates constraint) {
    }

    @Override
    public boolean isValid(EventForm form, ConstraintValidatorContext context) {
        return form.getStartingDateTime()
                .isBefore(form.getEndingDateTime());
    }
}