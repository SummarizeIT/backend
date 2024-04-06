package io.summarizeit.backend.dto.validator;

import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.annotation.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private String[] types;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.types = constraintAnnotation.types();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        return Arrays.asList(types).contains(multipartFile.getContentType());
    }
}