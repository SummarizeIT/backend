package io.summarizeit.backend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteUserRequest {
    @Email
    @Schema(name = "email", description = "email of the user", type = "String", example = "example@mail.com")
    private String email;
}
