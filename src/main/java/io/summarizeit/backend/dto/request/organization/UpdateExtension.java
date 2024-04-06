package io.summarizeit.backend.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "UpdateExtension", description = "Extension Update object")
public class UpdateExtension {
        // TODO: add extension validation
        @NotBlank(message = "{not_blank}")
        @Schema(name = "identifier", description = "Identifier of the extension", type = "String", example = "test-bank")
        private String identifier;
    
        @NotNull
        @Schema(name = "isEnabled", description = "Toggle extension", type = "Boolean", example = "true")
        private Boolean isEnabled;
    
        @NotNull
        @Schema(name = "isEnabledByDefault", description = "Toggle extension", type = "Boolean", example = "true")
        private Boolean isEnabledByDefault;
}
