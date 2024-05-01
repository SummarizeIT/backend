package io.summarizeit.backend.dto.response.role;

import java.util.UUID;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.summarizeit.backend.entity.Role;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RoleResponse extends AbstractBaseResponse {
    @Schema(name = "parentId", description = "UUID of the role", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0", requiredMode = RequiredMode.REQUIRED)
    private UUID id;

    @Schema(name = "name", description = "Name of the role", type = "String", example = "Sample Role", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Schema(name = "users", description = "Array of UUIDs representing users assigned to the role", type = "array", example = "[\"6f0766f8-8580-4ec9-8674-3dcd72f8188b\",\"b743611b-8488-4694-9c91-49f94f2dfb24\"]", requiredMode = RequiredMode.REQUIRED)
    private UUID[] users;

    @ArraySchema(schema = @Schema(implementation = String.class, requiredMode = RequiredMode.REQUIRED))
    private String[] adminPermissions;

    public static RoleResponse convert(Role role) {
        return RoleResponse.builder().id(role.getId()).name(role.getName())
                .users(role.getUsers().stream().map(u -> u.getId()).toArray(UUID[]::new))
                .adminPermissions(role.getAdminPermissions().stream().map(perm -> perm.toString()).toArray(String[]::new)).build();
    }
}
