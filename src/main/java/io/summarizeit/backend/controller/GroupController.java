package io.summarizeit.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.group.CreateGroupRequest;
import io.summarizeit.backend.dto.request.group.UpdateGroupRequest;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.group.GroupPaginationResponse;
import io.summarizeit.backend.dto.response.group.GroupResponse;
import io.summarizeit.backend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/{organizationId}/group")
@Tag(name = "Group", description = "Group API")
public class GroupController {
        private final GroupService groupService;

        @GetMapping
        @Operation(summary = "Get organization groups", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupPaginationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<GroupPaginationResponse> getUsers(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Parameter(description = "Query", required = true) @Valid final ListQuery queryRequest) {
                return ResponseEntity.ok(groupService.list(organizationId, queryRequest));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get organization group details by ID", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<GroupResponse> getGroupById(
                        @Parameter(description = "Group ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId) {
                return ResponseEntity.ok(groupService.getGroup(id, organizationId));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update organization group", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> updateGroup(
                        @Parameter(description = "Group ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @Valid @RequestBody final UpdateGroupRequest updateRequest) {
                groupService.updateOrganizationGroup(id, organizationId, updateRequest);
                return ResponseEntity.ok().build();
        }

        @PostMapping
        @Operation(summary = "Create organization group", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> createGroup(
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId,
                        @RequestBody @Valid final CreateGroupRequest createRequest) {
                groupService.createOrganizationGroup(organizationId, createRequest);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete organization group", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deleteGroup(
                        @Parameter(description = "Group ID", required = true) @PathVariable final UUID id,
                        @Parameter(description = "Organization ID", required = true) @PathVariable final UUID organizationId) {
                groupService.deleteOrganizationGroup(id, organizationId);
                return ResponseEntity.ok().build();
        }

}
