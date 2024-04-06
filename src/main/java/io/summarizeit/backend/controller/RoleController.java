package io.summarizeit.backend.controller;

import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.role.CreateRoleRequest;
import io.summarizeit.backend.dto.request.role.UpdateRoleRequest;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.role.RolePaginationResponse;
import io.summarizeit.backend.dto.response.role.RoleResponse;
import io.summarizeit.backend.service.RoleService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/{organizationId}/role")
@Tag(name = "Role", description = "Role API")
public class RoleController {
        private final RoleService roleService;

        @GetMapping
        @Operation(summary = "Get organization role details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RolePaginationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<RolePaginationResponse> getOrganizationRoles(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Role query", required = false) @Valid final ListQuery queryRequest) {
                return ResponseEntity.ok().body(roleService.list(organizationId, queryRequest));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get organization role details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<RoleResponse> getOrganizationRoleById(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Role ID", required = true) @PathVariable final UUID id) {
                return ResponseEntity.ok().body(roleService.getRole(id, organizationId));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update organization role", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> updateOrganizationRole(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Role ID", required = true) @PathVariable final UUID id,
                        @RequestBody @Valid final UpdateRoleRequest updateRequest) {
                roleService.updateOrganizationRole(id, organizationId, updateRequest);
                return ResponseEntity.ok().build();
        }

        @PostMapping
        @Operation(summary = "Create organization role", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> createOrganizationRole(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @RequestBody @Valid final CreateRoleRequest createRequest) {
                roleService.createOrganizationRole(organizationId, createRequest);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete organization role", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteOrganizationRole(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Role ID", required = true) @PathVariable final UUID id) {
                roleService.deleteOrganizationRole(id, organizationId);
                return ResponseEntity.ok().build();
        }
}
