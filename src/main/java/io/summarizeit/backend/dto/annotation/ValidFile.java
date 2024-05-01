package io.summarizeit.backend.dto.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.summarizeit.backend.dto.validator.FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { FileValidator.class })
public @interface ValidFile {
    String[] types();

    Class<?>[] groups() default {};

    String message() default "Invalid file type!";

    Class<? extends Payload>[] payload() default {};
}
