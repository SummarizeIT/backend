package io.summarizeit.backend.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
@RequiredArgsConstructor
public class ObjectJsonStringConverter implements AttributeConverter<Object, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Object object) {
        String objectString = null;
        try {
            objectString = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return objectString;
    }

    @Override
    public Object convertToEntityAttribute(String string) {
        Object object = null;
        try {
            object = objectMapper.readValue(string,
                    new TypeReference<Object>() {
                    });
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return object;
    }
}