package io.summarizeit.backend.dto.response.entry;

import java.time.LocalDateTime;
import java.util.List;

import io.summarizeit.backend.dto.ExtensionData;
import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class EntryResponse extends AbstractBaseResponse {
    @Schema(name = "title", type = "String", description = "Title of the entry", requiredMode = RequiredMode.REQUIRED)
    private String title;

    @Schema(name = "createdOn", type = "LocalDateTime", description = "Date when the entry was created", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime createdOn;

    @Builder.Default
    @Schema(name = "isPublic", description = "Toggle public status of entry", type = "Boolean", example = "true")
    private Boolean isPublic = false;

    @ArraySchema(schema = @Schema(implementation = ExtensionData.class, requiredMode = RequiredMode.REQUIRED))
    private List<ExtensionData> extensions;

    @Builder.Default
    @Schema(name = "isProcessing", type = "Boolean", description = "Indicates if the entry is still processing", requiredMode = RequiredMode.REQUIRED)
    private boolean isProcessing = true;

    @Schema(name = "url", type = "String", description = "URL of the media", requiredMode = RequiredMode.REQUIRED)
    private String url;

    @Schema(name = "transcript", type = "String", description = "transcript of the video", requiredMode = RequiredMode.REQUIRED)
    private String transcript;
}
