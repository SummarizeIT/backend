package io.summarizeit.backend.dto.response.auth;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TokenResponse extends AbstractBaseResponse {
    @Schema(name = "token", description = "Token", type = "String", example = "eyJhbGciOiJIUzUxMiJ9...", requiredMode = RequiredMode.REQUIRED)
    private String token;

    @Schema(name = "refreshToken", description = "Refresh Token", type = "String", example = "eyJhbGciOiJIUzUxMiJ9...", requiredMode = RequiredMode.REQUIRED)
    private String refreshToken;

    @Schema(name = "expiresIn", description = "Expires In", type = "TokenExpiresInResponse", requiredMode = RequiredMode.REQUIRED)
    private TokenExpiresInResponse expiresIn;
}
