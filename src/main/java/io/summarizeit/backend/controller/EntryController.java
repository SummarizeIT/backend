package io.summarizeit.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.annotation.ValidFile;
import io.summarizeit.backend.dto.request.entry.MoveEntryRequest;
import io.summarizeit.backend.dto.request.entry.UpdateEntryRequest;
import io.summarizeit.backend.dto.request.entry.UploadEntryRequest;
import io.summarizeit.backend.dto.response.DetailedErrorResponse;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.entry.EntryResponse;
import io.summarizeit.backend.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/entry")
@Tag(name = "Entry", description = "Entry API")
public class EntryController {
        private final EntryService entryService;

        @GetMapping("/{id}")
        @Operation(summary = "Get Entry details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<EntryResponse> getEntryById(
                        @Parameter(name = "id", description = "Entry ID", required = true) @PathVariable final UUID id) {
                return ResponseEntity.ok().body(entryService.getEntryById(id));
        }

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Upload a new entry", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> uploadEntry(
                        @Parameter(description = "Entry Details", required = true) final UploadEntryRequest uploadRequest,
                        @RequestBody @ValidFile(types = { "video/mp4", "audio/mpeg" }) final MultipartFile mediaFile)
                        throws IOException {
                entryService.uploadEntry(uploadRequest, mediaFile);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete entry and its data", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteEntry(
                        @Parameter(name = "token", description = "Entry ID", required = true) @PathVariable final UUID id) {
                entryService.deleteEntry(id);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update an entry", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateEntry(
                        @Parameter(description = "ID of entry to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to rename", required = true) @RequestBody final UpdateEntryRequest renameRequest) {
                // TODO: Implementation
                throw new UnsupportedOperationException();
        }

        @PutMapping("/{id}/move")
        @Operation(summary = "Move an entry", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> moveEntry(
                        @Parameter(description = "ID of entry to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to move", required = true) @RequestBody @Valid final MoveEntryRequest moveRequest) {
                entryService.moveEntry(id, moveRequest);
                return ResponseEntity.ok().build();
        }
}
