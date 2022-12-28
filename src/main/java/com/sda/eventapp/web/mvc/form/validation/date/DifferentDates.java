package com.sda.eventapp.web.mvc.form.validation.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DifferentDatesValidator.class})
public @interface DifferentDates {
    String message() default "{com.sda.eventapp.web.mvc.form.validation.date.DifferentDates.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}