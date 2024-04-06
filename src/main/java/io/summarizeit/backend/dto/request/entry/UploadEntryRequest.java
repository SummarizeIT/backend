package io.summarizeit.backend.dto.request.entry;

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
public class UploadEntryRequest {
    @NotBlank(message = "{not_blank}")
    @Size(max = 430, message = "{max_length}")
    @Schema(name = "title", description = "Lastname of the user", type = "String", requiredMode = Schema.RequiredMode.REQUIRED, example = "DOE")
    private String title;
}
