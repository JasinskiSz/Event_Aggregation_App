package com.sda.eventapp.dto.form.validation.constraint;

import com.sda.eventapp.dto.form.validation.validator.MaxEventTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MaxEventTimeValidator.class})
public @interface MaxEventTime {
    String message() default "{com.sda.eventapp.web.mvc.form.validation.constraints.MaxEventTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
