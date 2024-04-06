package io.summarizeit.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.summarizeit.backend.dto.AdminPermissions;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PermissionListConverter implements AttributeConverter<List<AdminPermissions>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<AdminPermissions> permissions) {
        return permissions != null ? String.join(SPLIT_CHAR, permissions.stream().map(perm -> perm.toString()).toList())
                : "";
    }

    @Override
    public List<AdminPermissions> convertToEntityAttribute(String string) {
        return (string != null && !string.isBlank())
                ? Arrays.asList(string.split(SPLIT_CHAR)).stream().map(str -> AdminPermissions.valueOf(str)).toList()
                : new ArrayList<AdminPermissions>();
    }
}