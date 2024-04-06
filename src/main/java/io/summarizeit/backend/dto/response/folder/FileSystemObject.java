package io.summarizeit.backend.dto.response.folder;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Schema(description = "Directory or File in the file system")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileSystemObject {
    @Schema(name = "id", description = "UUID of the object", type = "String", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0")
    private UUID id;

    @Schema(name = "name", type = "String", description = "Name of the object", example = "CS")
    private String name;
}
