package io.summarizeit.backend.dto.request.folder;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class CreateFolderRequest extends UpdateFolderRequest {
    @Schema(name = "parentId", description = "UUID of the parent directory", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0")
    private UUID parentId;
}
