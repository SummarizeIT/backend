package io.summarizeit.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ErrorResponse extends AbstractBaseResponse {
    @Schema(name = "message", description = "Response messages field", type = "String", example = "This is message field", requiredMode = RequiredMode.REQUIRED)
    private String message;
}
