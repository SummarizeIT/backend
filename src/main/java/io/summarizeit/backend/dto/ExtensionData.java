package io.summarizeit.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Extension", description = "Extension associated with an entry")
public class ExtensionData {
    // TODO: add extension identifier validation
    @NotBlank(message = "{not_blank}")
    @Schema(name = "identifier", description = "Identifier of the extension", type = "String", example = "test-bank")
    private String identifier;

    @Schema(name = "content", description = "Content of the extension", type = "object", example = "{\"key\":\"value\"}")
    private Object content;
}
