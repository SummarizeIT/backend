package io.summarizeit.backend.controller;

import io.summarizeit.backend.dto.annotation.ValidFile;
import io.summarizeit.backend.dto.request.me.UpdateMeRequest;
import io.summarizeit.backend.dto.response.DetailedErrorResponse;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.SuccessResponse;
import io.summarizeit.backend.dto.response.me.MeResponse;
import io.summarizeit.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "Account", description = "Account API")
public class AccountController {
        private final AccountService accountService;

        @GetMapping
        @Operation(summary = "Me", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<MeResponse> me() {
                return ResponseEntity.ok().body(accountService.getMe());
        }

        @PutMapping
        @Operation(summary = "Update me", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateMe(
                        @RequestBody @Valid UpdateMeRequest request) {
                accountService.updateMe(request);
                return ResponseEntity.ok().build();
        }

        @PutMapping(path = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Update my avatar", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<Void> updateMyAvatar(
                        @RequestBody @ValidFile(types = { "image/png", "image/jpg",
                                        "image/jpeg" }) final MultipartFile avatar)
                        throws IOException {
                accountService.updateMyAvatar(avatar);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("avatar")
        @Operation(summary = "Clear my avatar", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(schema = @Schema(hidden = true))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> clearMyAvatar() throws IOException {
                accountService.clearMyAvatar();
                return ResponseEntity.ok().build();
        }

        @PostMapping("/invite/{organizationId}")
        @Operation(summary = "Accept invite", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))) })
        public ResponseEntity<SuccessResponse> acceptInvite(
                        @Parameter(description = "ID of the invite", required = true) @PathVariable final UUID organizationId) {
                accountService.acceptOrganizationInvite(organizationId);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/invite/{organizationId}")
        @Operation(summary = "Decline invite", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))) })
        public ResponseEntity<SuccessResponse> declineInvite(
                        @Parameter(description = "ID of the invite", required = true) @PathVariable final UUID organizationId) {
                accountService.declineOrganizationInvite(organizationId);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/organization/{organizationId}")
        @Operation(summary = "Leave organization", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))) })
        public ResponseEntity<SuccessResponse> leaveOrganization(
                        @Parameter(description = "ID of the organization", required = true) @PathVariable final UUID organizationId) {
                accountService.leaveOrganization(organizationId);
                return ResponseEntity.ok().build();
        }
}
