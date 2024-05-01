package io.summarizeit.backend.dto.response.entry;

import java.time.Instant;
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

    @Schema(name = "createdOn", type = "Date", description = "Date when the entry was created", requiredMode = RequiredMode.REQUIRED)
    private Instant createdOn;

    @Builder.Default
    @Schema(name = "body", type = "String", description = "Body content of the entry", requiredMode = RequiredMode.REQUIRED)
    private String body = "";

    @ArraySchema(schema = @Schema(implementation = ExtensionData.class, requiredMode = RequiredMode.REQUIRED))
    private List<ExtensionData> extensions;

    @Builder.Default
    @Schema(name = "isProcessing", type = "Boolean", description = "Indicates if the entry is still processing", requiredMode = RequiredMode.REQUIRED)
    private boolean isProcessing = true;

    @Getter
    @Schema(name = "mediaType", description = "Type of the entry", requiredMode = RequiredMode.REQUIRED)
    private String mediaType;

    @Schema(name = "url", type = "String", description = "URL of the media", requiredMode = RequiredMode.REQUIRED)
    private String url;
}
