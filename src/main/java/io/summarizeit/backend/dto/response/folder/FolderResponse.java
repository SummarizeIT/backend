package io.summarizeit.backend.dto.response.folder;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@Schema(name = "Video", description = "Folder in the file system", requiredMode = RequiredMode.REQUIRED)
public class FolderResponse extends AbstractBaseResponse {
    @ArraySchema(schema = @Schema(implementation = DirectoryBreadcrumbsResponse.class, requiredMode = RequiredMode.REQUIRED))
    private List<DirectoryBreadcrumbsResponse> pathFromRoot;

    @ArraySchema(schema = @Schema(anyOf = { FolderObjectResponse.class, MediaResponse.class }, requiredMode = RequiredMode.REQUIRED))
    private List<FileSystemObject> list; // Directory, Audio, Video

    @Schema(name = "groups", description = "Array of UUIDs representing groups assigned to the folder", type = "array", example = "[\"6f0766f8-8580-4ec9-8674-3dcd72f8188b\",\"b743611b-8488-4694-9c91-49f94f2dfb24\"]", requiredMode = RequiredMode.REQUIRED)
    private List<UUID> groups;
}