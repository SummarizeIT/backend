package io.summarizeit.backend.dto.response.auth;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.summarizeit.backend.entity.PasswordResetToken;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class PasswordResetResponse extends AbstractBaseResponse {
    @Schema(name = "id", description = "UUID", type = "String", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0", requiredMode = RequiredMode.REQUIRED)
    private String id;

    @Schema(name = "token", description = "Token", type = "String", example = "KQJGpJ...", requiredMode = RequiredMode.REQUIRED)
    private String token;

    @Schema(name = "userId", description = "User ID", type = "String", requiredMode = RequiredMode.REQUIRED)
    private UUID userId;

    @Schema(name = "expirationDate", description = "Expiration date", type = "String", example = "2003-09-15 12:34:46.7", requiredMode = RequiredMode.REQUIRED)
    private Date expirationDate;

    @Schema(name = "createdAt", description = "Date time field of user creation", type = "LocalDateTime", example = "2002-09-29T22:37:31", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", type = "LocalDateTime", description = "Date time field of user update", example = "2002-09-29T22:37:31", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime updatedAt;

    /**
     * Convert PasswordResetToken to PasswordResetResponse
     *
     * @param passwordResetToken PasswordResetToken
     * @return PasswordResetResponse
     */

    public static PasswordResetResponse convert(PasswordResetToken passwordResetToken) {
        return PasswordResetResponse.builder()
                .id(passwordResetToken.getId().toString())
                .token(passwordResetToken.getToken())
                .userId(passwordResetToken.getUser().getId())
                .expirationDate(passwordResetToken.getExpirationDate())
                .build();
    }
}
