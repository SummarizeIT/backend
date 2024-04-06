package io.summarizeit.backend.dto.response.me;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "Invites", description = "User invites to organizations")
public class InviteResponse {
    @Schema(name = "id", description = "Organization UUID", type = "UUID", example = "91b2999d-d327-4dc8-9956-2fadc0dc8778")
    private UUID id;

    @Schema(name = "name", description = "Name of the organization", type = "String", example = "John")
    private String name;

    @Schema(name = "avatar", description = "URL of the organization's profile image", type = "String", example = "https://i.imgur.com/xapoQq3.jpeg")
    private String avatar;
}
