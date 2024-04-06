package io.summarizeit.backend.controller;

import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.user.InviteUserRequest;
import io.summarizeit.backend.dto.request.user.UpdateUserRequest;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.user.UserPaginationResponse;
import io.summarizeit.backend.dto.response.user.UserResponse;
import io.summarizeit.backend.service.OrganizationService;
import io.summarizeit.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/{organizationId}/user")
@Tag(name = "User", description = "User API")
public class UserController {
        private final UserService userService;

        private final OrganizationService organizationService;

        @GetMapping
        @Operation(summary = "Get organization users", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserPaginationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<UserPaginationResponse> getUsers(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Query", required = true) @Valid final ListQuery queryRequest) {
                return ResponseEntity.ok(userService.list(queryRequest, organizationId));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get organization user details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<UserResponse> getUserById(
                        @Parameter(description = "User ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId) {
                return ResponseEntity.ok(userService.getOrganizationUser(id, organizationId));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update organization user", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> updateUser(
                        @Parameter(description = "User ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @RequestBody @Valid final UpdateUserRequest updateRequest) {

                userService.updateOrganizationUser(id, organizationId, updateRequest);
                return ResponseEntity.ok().build();
        }

        @PostMapping
        @Operation(summary = "Invite user", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> inviteUser(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Create request body") @RequestBody @Valid final InviteUserRequest createRequest) {
                organizationService.inviteUserToOrganization(createRequest, organizationId);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Kick user", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteUser(
                        @Parameter(description = "User ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId) {
                organizationService.kickUserFromOrganization(id, organizationId);
                return ResponseEntity.ok().build();
        }
}
