package io.summarizeit.backend.dto.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ExtensionInstance", description = "Extension information", requiredMode = RequiredMode.REQUIRED)
public class ExtensionInstance {
    @Schema(name = "identifier", description = "Identifier of the extension", type = "String", example = "test-bank", requiredMode = RequiredMode.REQUIRED)
    private String identifier;

    @Schema(name = "name", description = "Name of the extension", type = "String", example = "Test Bank Extension", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Schema(name = "description", description = "Description of the extension", type = "String", example = "The test bank extension allows editors to write quick questions to test viewers.", requiredMode = RequiredMode.REQUIRED)
    private String description;

    @Schema(name = "version", description = "Version of the extension", type = "String", example = "1.0.0", requiredMode = RequiredMode.REQUIRED)
    private String version;

    @Schema(name = "isEnabled", description = "Toggle extension", type = "Boolean", example = "true", requiredMode = RequiredMode.REQUIRED)
    private Boolean isEnabled;

    @Schema(name = "isEnabledByDefault", description = "Toggle extension", type = "Boolean", example = "true", requiredMode = RequiredMode.REQUIRED)
    private Boolean isEnabledByDefault;
}