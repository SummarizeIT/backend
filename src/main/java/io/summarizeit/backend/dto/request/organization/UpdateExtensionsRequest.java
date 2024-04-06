package io.summarizeit.backend.dto.request.organization;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateExtensionsRequest {
    // TODO: add extension validation
    @NotBlank
    @ArraySchema(schema = @Schema(implementation = UpdateExtension.class))
    private UpdateExtension[] extensions;
}
