package io.summarizeit.backend.dto.response.organization;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtensionsResponse {
    @ArraySchema(schema = @Schema(implementation = ExtensionInstance.class, requiredMode = RequiredMode.REQUIRED))
    private ExtensionInstance[] extensions;
}