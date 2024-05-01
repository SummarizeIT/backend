package io.summarizeit.backend.dto.request.role;

import java.util.List;
import java.util.UUID;

import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.dto.annotation.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Pattern(regexp = "^(?!Default|Admin).+$")
    @Schema(name = "name", description = "Name of the role", type = "String", example = "Sample Role")
    private String name;

    @Schema(name = "users", description = "Array of UUIDs representing users assigned to the role", type = "array", example = "[\"6f0766f8-8580-4ec9-8674-3dcd72f8188b\",\"b743611b-8488-4694-9c91-49f94f2dfb24\"]")
    private UUID[] users;

    @ValueOfEnum(enumClass = AdminPermissions.class)
    @ArraySchema(schema = @Schema(implementation = AdminPermissions.class))
    private List<String> adminPermissions;
}