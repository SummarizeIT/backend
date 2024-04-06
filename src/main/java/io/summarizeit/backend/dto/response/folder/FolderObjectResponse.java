package io.summarizeit.backend.dto.response.folder;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class FolderObjectResponse extends FileSystemObject{
    @Schema(name = "isPublic", type = "Boolean", description = "Indicates if the directory is public", example = "false")
    private Boolean isPublic;
}
