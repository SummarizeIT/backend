package io.summarizeit.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SuccessResponse extends AbstractBaseResponse {
    @Schema(name = "message", type = "Integer", description = "Response message field", example = "Success!", requiredMode = RequiredMode.REQUIRED)
    private String message;
}
