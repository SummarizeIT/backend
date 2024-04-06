package io.summarizeit.backend.dto.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.summarizeit.backend.dto.validator.SortValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

@Target(ElementType.FIELD)
@Constraint(validatedBy = { SortValidator.class})
@Retention(RUNTIME)
@Pattern(regexp = "^(.*(?::(asc|desc)))$")
public @interface Sort {
    String[] value();

    String message() default "Must be a valid sort type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}