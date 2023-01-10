package com.sda.eventapp.web.mvc.form.validation.validator;

import com.sda.eventapp.web.mvc.form.EventForm;
import com.sda.eventapp.web.mvc.form.validation.constraint.MaxEventTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxEventTimeValidator implements ConstraintValidator<MaxEventTime, EventForm> {

    @Override
    public void initialize(MaxEventTime constraint) {
    }

    @Override
    public boolean isValid(EventForm form, ConstraintValidatorContext context) {
        return (form.getStartingDateTime().plusHours(336).isAfter(form.getEndingDateTime()));
    }
}
