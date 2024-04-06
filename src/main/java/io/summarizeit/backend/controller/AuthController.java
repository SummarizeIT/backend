package io.summarizeit.backend.controller;

import io.summarizeit.backend.dto.request.auth.LoginRequest;
import io.summarizeit.backend.dto.request.auth.PasswordRequest;
import io.summarizeit.backend.dto.request.auth.RegisterRequest;
import io.summarizeit.backend.dto.request.auth.ResetPasswordRequest;
import io.summarizeit.backend.dto.response.DetailedErrorResponse;
import io.summarizeit.backend.dto.response.ErrorResponse;
import io.summarizeit.backend.dto.response.SuccessResponse;
import io.summarizeit.backend.dto.response.auth.PasswordResetResponse;
import io.summarizeit.backend.dto.response.auth.TokenResponse;
import io.summarizeit.backend.service.AuthService;
import io.summarizeit.backend.service.MessageSourceService;
import io.summarizeit.backend.service.PasswordResetTokenService;
import io.summarizeit.backend.service.UserService;
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
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.summarizeit.backend.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
        private final AuthService authService;

        private final UserService userService;

        private final PasswordResetTokenService passwordResetTokenService;

        private final MessageSourceService messageSourceService;

        @PostMapping("/login")
        @Operation(summary = "Login")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<TokenResponse> login(
                        @Parameter(description = "Request body to login", required = true) @RequestBody @Validated final LoginRequest request) {
                return ResponseEntity.ok(
                                authService.login(request.getEmail(), request.getPassword(), request.getRememberMe()));
        }

        @PostMapping("/register")
        @Operation(summary = "Register")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DetailedErrorResponse.class)))
        })
        public ResponseEntity<SuccessResponse> register(
                        @Parameter(description = "Request body to register", required = true) @RequestBody @Valid final RegisterRequest request)
                        throws BindException {
                userService.register(request);

                return ResponseEntity.ok(
                                SuccessResponse.builder().message(messageSourceService.get("registered_successfully"))
                                                .build());
        }

        @GetMapping("/refresh")
        @Operation(summary = "Refresh")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<TokenResponse> refresh(
                        @Parameter(description = "Refresh token", required = true) @RequestHeader("Authorization") @Validated final String refreshToken) {
                return ResponseEntity.ok(authService.refreshFromBearerString(refreshToken));
        }

        @PostMapping("/reset-password")
        @Operation(summary = "Reset password")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<SuccessResponse> resetPassword(
                        @Parameter(description = "Request body to password", required = true) @RequestBody @Valid final PasswordRequest request) {
                authService.resetPassword(request.getEmail());

                return ResponseEntity.ok(SuccessResponse.builder()
                                .message(messageSourceService.get("password_reset_link_sent"))
                                .build());
        }

        @GetMapping("/reset-password/{token}")
        @Operation(summary = "Reset password check token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordResetResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<PasswordResetResponse> resetPassword(
                        @Parameter(description = "Password reset token", required = true) @PathVariable final String token) {
                return ResponseEntity.ok(PasswordResetResponse.convert(passwordResetTokenService.findByToken(token)));
        }

        @PostMapping("/reset-password/{token}")
        @Operation(summary = "Reset password with token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordResetResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<SuccessResponse> resetPassword(
                        @Parameter(description = "Password reset token", required = true) @PathVariable final String token,
                        @Parameter(description = "Request body to update password", required = true) @RequestBody @Valid ResetPasswordRequest request) {
                userService.resetPassword(token, request);

                return ResponseEntity.ok(SuccessResponse.builder()
                                .message(messageSourceService.get("password_reset_success_successfully"))
                                .build());
        }

        @GetMapping("/logout")
        @Operation(summary = "Logout", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<SuccessResponse> logout() {
                authService.logout(userService.getUser());

                return ResponseEntity.ok(SuccessResponse.builder()
                                .message(messageSourceService.get("logout_successfully"))
                                .build());
        }
}
