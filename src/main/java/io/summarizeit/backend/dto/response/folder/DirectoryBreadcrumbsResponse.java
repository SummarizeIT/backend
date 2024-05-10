package io.summarizeit.backend.dto.response.folder;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DirectoryBreadcrumbsResponse {
    @Schema(name = "id", description = "UUID of the directory", type = "String", example = "127j5663-422a-4a98-8c7b-19166fd8a0b0", requiredMode = RequiredMode.REQUIRED)
    private UUID id;

    @Schema(name = "name", type = "String", description = "Name of the directory", example = "CS", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Builder.Default
    @Schema(name = "isDir", type = "Boolean", description = "Indicates if the directory is a directory", requiredMode = RequiredMode.REQUIRED)
    private Boolean isDir = true;
}
