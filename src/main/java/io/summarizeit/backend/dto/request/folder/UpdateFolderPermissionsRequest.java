package io.summarizeit.backend.dto.request.folder;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class UpdateFolderPermissionsRequest {
    @ArraySchema(schema = @Schema(name = "groups", description = "UUID array of group ids", type = "String", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0"))
    private List<UUID> groups;

    @NotNull(message = "{not_blank}")
    @Schema(name = "isPublic", description = "Indicates if the entry is public", type = "boolean", example = "true")
    private boolean isPublic;
}
