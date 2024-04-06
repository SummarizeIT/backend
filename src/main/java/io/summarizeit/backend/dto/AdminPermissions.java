package io.summarizeit.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "AdminPermissions", description = "Organization admin permissions for the user")
public enum AdminPermissions {
    ADMIN_ROLES, ADMIN_USERS, ADMIN_GROUPS, ADMIN_MEDIA
}
