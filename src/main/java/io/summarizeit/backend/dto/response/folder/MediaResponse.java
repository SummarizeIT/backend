package io.summarizeit.backend.dto.response.folder;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Schema(name = "Audio", description = "Audio file in the file system", requiredMode = RequiredMode.REQUIRED)
@SuperBuilder
public class MediaResponse extends FileSystemObject {
}

