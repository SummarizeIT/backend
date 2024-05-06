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
public class TokenExpiresInResponse extends AbstractBaseResponse {
    @Schema(name = "token", description = "Token expires In", type = "Long", example = "3600", requiredMode = RequiredMode.REQUIRED)
    private Long token;

    @Schema(name = "refreshToken", description = "Refresh token expires In", type = "Long", example = "3600", requiredMode = RequiredMode.REQUIRED)
    private Long refreshToken;
}
