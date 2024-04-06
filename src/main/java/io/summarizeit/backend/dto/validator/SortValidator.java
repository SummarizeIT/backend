package io.summarizeit.backend.dto.validator;

import java.util.Arrays;

import io.summarizeit.backend.dto.annotation.Sort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class SortValidator implements ConstraintValidator<Sort, String> {
    private String[] value;

    @Override
    public void initialize(final Sort inAnnotation) {
        value = inAnnotation.value();
    }

    @Override
    public boolean isValid(final String str, final ConstraintValidatorContext context) {
        return Arrays.binarySearch(value, str.split(":")[0]) >= 0;
    }
}
