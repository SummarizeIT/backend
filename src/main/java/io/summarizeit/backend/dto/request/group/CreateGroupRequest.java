package io.summarizeit.backend.dto.request.group;

import java.util.UUID;

import io.summarizeit.backend.dto.GroupLeaderDto;
import io.summarizeit.backend.dto.annotation.Color;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateGroupRequest {
    @Color
    @Schema(name = "color", description = "Color code of the group", type = "String", example = "FF0000")
    private String color;

    @NotBlank(message = "{not_blank}")
    @Schema(name = "name", description = "Name of the group", type = "String", example = "Sample Group")
    private String name;

    @Schema(name = "users", description = "Array of UUIDs representing users in the group", type = "array", example = "[\"6f0766f8-8580-4ec9-8674-3dcd72f8188b\",\"b743611b-8488-4694-9c91-49f94f2dfb24\"]")
    private UUID[] users;

    @ArraySchema(schema = @Schema(implementation = GroupLeaderDto.class))
    private GroupLeaderDto[] groupLeaders;
}
