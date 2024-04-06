package io.summarizeit.backend.dto.request.me;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMeRequest {
    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Schema(name = "firstName", description = "Name of the user", type = "String", requiredMode = Schema.RequiredMode.REQUIRED, example = "John")
    private String firstName;

    @NotBlank(message = "{not_blank}")
    @Size(max = 50, message = "{max_length}")
    @Schema(name = "lastName", description = "Lastname of the user", type = "String", requiredMode = Schema.RequiredMode.REQUIRED, example = "DOE")
    private String lastName;
}