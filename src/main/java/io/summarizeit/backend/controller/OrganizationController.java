package io.summarizeit.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.annotation.ValidFile;
import io.summarizeit.backend.dto.request.organization.CreateOrganizationRequest;
import io.summarizeit.backend.dto.request.organization.UpdateExtensionsRequest;
import io.summarizeit.backend.dto.request.organization.UpdateOrganizationRequest;
import io.summarizeit.backend.dto.response.DetailedErrorResponse;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.organization.ExtensionsResponse;
import io.summarizeit.backend.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
@Tag(name = "Organization", description = "Organization API")
public class OrganizationController {
        private final OrganizationService organizationService;

        @PutMapping(path = "/{id}")
        @Operation(summary = "Update an organization", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateOrganization(
                        @Parameter(description = "ID of organization to update", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Request body to update", required = true) @RequestBody final UpdateOrganizationRequest updateRequest) {
                organizationService.updateOrganization(id, updateRequest);
                return ResponseEntity.ok().build();
        }

        @PutMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Update an organization avatar", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateOrganizationPicture(
                        @Parameter(description = "ID of organization to update", required = true) @PathVariable final UUID id,
                        @RequestBody @ValidFile(types = { "image/png", "image/jpg",
                                        "image/jpeg" }) final MultipartFile avatar)
                        throws IOException {
                organizationService.updateOrganizationAvatar(id, avatar);
                return new ResponseEntity<>(HttpStatus.OK);
        }

        @DeleteMapping("/{id}/avatar")
        @Operation(summary = "Clear organization avatar", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteOrganizationAvatar(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID id) {
                organizationService.deleteOrganizationAvatar(id);
                return new ResponseEntity<>(HttpStatus.OK);
        }

        @PostMapping
        @Operation(summary = "Create organization", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> createOrganization(
                        @Parameter(description = "Create organization request") @RequestBody @Valid final CreateOrganizationRequest createRequest) {
                organizationService.createOrganization(createRequest);
                return new ResponseEntity<>(HttpStatus.CREATED);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete organization", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteOrganization(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID id) {
                organizationService.deleteOrganization(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @GetMapping("/{id}/extensions")
        @Operation(summary = "Extensions", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExtensionsResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<ExtensionsResponse> extensions(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID id) {
                // TODO: Implementation
                throw new UnsupportedOperationException();
        }

        @PutMapping("/{id}/extensions")
        @Operation(summary = "Update organization extensions", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> updateExtensions(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Update request body") @RequestBody final UpdateExtensionsRequest updateRequest) {
                // TODO: Implementation
                throw new UnsupportedOperationException();
        }
}