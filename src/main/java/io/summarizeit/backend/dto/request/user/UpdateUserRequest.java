package io.summarizeit.backend.dto.request.user;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @ArraySchema(schema = @Schema(name = "groupIds", description = "ID of the group", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0"))
    private List<UUID> groupIds;

    @ArraySchema(schema = @Schema(name = "roleIds", description = "ID of the role", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0"))
    private List<UUID> roleIds;
}
