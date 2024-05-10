package io.summarizeit.backend.dto.request.entry;

import java.util.Set;

import io.summarizeit.backend.dto.ExtensionData;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEntryRequest {
    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Schema(name = "title", description = "Title of the entry", type = "String", example = "Sample Title")
    private String title;

    @Builder.Default
    @Schema(name = "isPublic", description = "Toggle public status of entry", type = "Boolean", example = "true")
    private Boolean isPublic = false;

    @ArraySchema(schema = @Schema(implementation = ExtensionData.class))
    private Set<ExtensionData> extensions;
}
