package io.summarizeit.backend.dto.response.me;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class MeResponse extends AbstractBaseResponse {
    @Schema(name = "id", description = "UUID", type = "UUID", example = "91b2999d-d327-4dc8-9956-2fadc0dc8778")
    private UUID id;

    @Schema(name = "email", description = "E-mail of the user", type = "String", example = "mail@example.com")
    private String email;

    @Schema(name = "firstName", description = "Name of the user", type = "String", example = "John")
    private String firstName;

    @Schema(name = "lastName", description = "Lastname of the user", type = "String", example = "DOE")
    private String lastName;

    @Schema(name = "rootFolder", description = "UUID for the user's root folder", type = "String", example = "9265k39d-d327-4dc8-9956-2fadc0dc8778")
    private UUID rootFolder;

    @Schema(name = "avatar", description = "URL of the user's profile image", type = "String", example = "https://i.imgur.com/xapoQq3.jpeg")
    private String avatar;

    @ArraySchema(schema = @Schema(implementation = UserOrganization.class))
    private List<UserOrganization> organizations;

    @ArraySchema(schema = @Schema(implementation = InviteResponse.class))
    private List<InviteResponse> invites;
}
