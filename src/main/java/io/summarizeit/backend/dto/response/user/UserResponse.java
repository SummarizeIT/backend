package io.summarizeit.backend.dto.response.user;

import java.util.List;
import java.util.UUID;

import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.summarizeit.backend.entity.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UserResponse extends AbstractBaseResponse {
    @Schema(name = "id", description = "UUID of the user", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0", requiredMode = RequiredMode.REQUIRED)
    private UUID id;

    @Schema(name = "imageUrl", description = "URL of the user's image", type = "String", example = "https://example.com/image.jpg", requiredMode = RequiredMode.REQUIRED)
    private String imageUrl;

    @Schema(name = "name", description = "First name of the user", type = "String", example = "John", requiredMode = RequiredMode.REQUIRED)
    private String firstName;

    @Schema(name = "lastname", description = "Last name of the user", type = "String", example = "Doe", requiredMode = RequiredMode.REQUIRED)
    private String lastName;

    @Schema(name = "email", description = "Email address of the user", type = "String", example = "john.doe@example.com", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @ArraySchema(schema = @Schema(name = "groupIds", description = "Group UUID that the user belongs to", type = "UUID", example = "6f0766f8-8580-4ec9-8674-3dcd72f8188b", requiredMode = RequiredMode.REQUIRED))
    private List<UUID> groupIds;

    @ArraySchema(schema = @Schema(name = "roleIds", description = "Role UUID assigned to the user", type = "UUID", example = "6f0766f8-8580-4ec9-8674-3dcd72f8188b", requiredMode = RequiredMode.REQUIRED))
    private List<UUID> roleIds;

    public static UserResponse convert(User user) {
        return UserResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail())
                .id(user.getId())
                .groupIds((List<UUID>) user.getGroups().stream().map(g -> g.getId()).toList())
                .roleIds((List<UUID>) user.getRoles().stream().map(r -> r.getId()).toList()).build();
    }
}
