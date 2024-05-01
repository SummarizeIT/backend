package io.summarizeit.backend.dto.response.folder;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class FolderObjectResponse extends FileSystemObject{
    //@Schema(name = "isPublic", type = "Boolean", description = "Indicates if the directory is public", example = "false", requiredMode = RequiredMode.REQUIRED)
    //private Boolean isPublic;

    @Schema(name = "isDir", type = "Boolean", description = "Indicates if the object is a directory", example = "true", requiredMode = RequiredMode.REQUIRED)
    @Builder.Default
    private final Boolean isDir = true;
}
