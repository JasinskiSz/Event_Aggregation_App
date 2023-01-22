package com.sda.eventapp.dto.form.validation.constraint;

import com.sda.eventapp.dto.form.validation.validator.EqualPasswordsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EqualPasswordsValidator.class})
public @interface EqualPasswords {
    String message() default "{com.sda.eventapp.web.mvc.form.validation.constraints.EqualPasswords.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
