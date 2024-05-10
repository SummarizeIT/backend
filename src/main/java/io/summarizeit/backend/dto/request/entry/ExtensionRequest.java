package io.summarizeit.backend.dto.request.entry;

import io.summarizeit.backend.dto.request.ExtensionPayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
public class ExtensionRequest {
    @Schema(name = "identifier", description = "Extension identifier", type = "String", example = "body")
    private String identifier;

    @Schema(name = "command", description = "Extension command", type = "String", example = "generate")
    private String command;

    @Schema(name = "payload", description = "Extension payload", type = "String", example = "body")
    private ExtensionPayload payload;
}
