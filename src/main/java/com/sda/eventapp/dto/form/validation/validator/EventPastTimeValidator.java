package com.sda.eventapp.dto.form.validation.validator;

import com.sda.eventapp.dto.form.EventForm;
import com.sda.eventapp.dto.form.validation.constraint.EventPastTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventPastTimeValidator implements ConstraintValidator<EventPastTime, EventForm> {

    @Override
    public void initialize(EventPastTime constraint) {
    }

    @Override
    public boolean isValid(EventForm form, ConstraintValidatorContext context) {
        return form.getStartingDateTime().isAfter(LocalDateTime.now());
    }
}
