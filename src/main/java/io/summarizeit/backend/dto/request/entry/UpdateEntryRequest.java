package io.summarizeit.backend.dto.request.entry;

import java.util.List;

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

    @NotBlank(message = "{not_blank}")
    @Size(max = 10000, message = "{max_length}")
    @Schema(name = "body", description = "Body content of the entry", type = "String", example = "Sample body content.")
    private String body;

    @ArraySchema(schema = @Schema(implementation = ExtensionData.class))
    private List<ExtensionData> extensions;
}
