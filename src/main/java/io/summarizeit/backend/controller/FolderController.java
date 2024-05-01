package io.summarizeit.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.summarizeit.backend.dto.request.folder.CreateFolderRequest;
import io.summarizeit.backend.dto.request.folder.MoveFolderRequest;
import io.summarizeit.backend.dto.request.folder.UpdateFolderPermissionsRequest;
import io.summarizeit.backend.dto.request.folder.UpdateFolderRequest;
import io.summarizeit.backend.dto.response.DetailedErrorResponse;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.folder.FolderResponse;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.service.FolderService;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/folder")
@Tag(name = "Folder", description = "Folder API")
public class FolderController {
        private final FolderService folderService;

        @GetMapping("/{id}")
        @Operation(summary = "Get folder details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FolderResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<FolderResponse> getFolderById(
                        @Parameter(name = "id", description = "Folder ID", required = true) @PathVariable final UUID id) {
                return ResponseEntity.ok().body(folderService.getFolderById(id));
        }

        @PostMapping
        @Operation(summary = "Create a new folder", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Folder> createFolder(
                        @Parameter(description = "Folder Details", required = true) @RequestBody final CreateFolderRequest createRequest) {
                folderService.createFolder(createRequest);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete folder and its contents", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteFolder(
                        @Parameter(name = "id", description = "Entry ID", required = true) @PathVariable final UUID id) {
                folderService.deleteFolder(id);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a folder", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateFolder(
                        @Parameter(description = "ID of folder to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to rename", required = true) @RequestBody final UpdateFolderRequest updateFolderRequest) {
                folderService.updateFolder(id, updateFolderRequest);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}/permissions")
        @Operation(summary = "Update a folder's permissions", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateFolderGroups(
                        @Parameter(description = "ID of folder to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to rename", required = true) @RequestBody final UpdateFolderPermissionsRequest updateRequest) {
                folderService.updateFolderPermissions(id, updateRequest);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}/move")
        @Operation(summary = "Move a folder", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> moveFolder(
                        @Parameter(description = "ID of folder to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to move", required = true) @RequestBody @Valid final MoveFolderRequest moveFolderRequest) {
                folderService.moveFolder(id, moveFolderRequest);
                return ResponseEntity.ok().build();
        }
}
