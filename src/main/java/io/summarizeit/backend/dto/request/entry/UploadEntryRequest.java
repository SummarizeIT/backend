package io.summarizeit.backend.dto.request.entry;

import java.util.UUID;

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
    @Schema(name = "parentFolderId", description = "ID of the destination folder where the entry will be uploaded", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0")
    private UUID parentFolderId;

    @NotBlank(message = "{not_blank}")
    @Size(max = 430, message = "{max_length}")
    @Schema(name = "title", description = "Lastname of the user", type = "String", requiredMode = Schema.RequiredMode.REQUIRED, example = "DOE")
    private String title;
}
