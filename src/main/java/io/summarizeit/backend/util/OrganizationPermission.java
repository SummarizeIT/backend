package io.summarizeit.backend.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.summarizeit.backend.dto.AdminPermissions;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrganizationPermission {
    AdminPermissions[] permissions();
}
