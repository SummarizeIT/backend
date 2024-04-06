package io.summarizeit.backend.dto.request.entry;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
public class MoveEntryRequest {
    @Schema(name = "destinationFolderId", description = "ID of the destination folder where the folder will be copied", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0")
    private UUID destinationFolderId;
}
