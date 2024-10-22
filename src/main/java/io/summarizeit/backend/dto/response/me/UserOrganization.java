package io.summarizeit.backend.dto.response.me;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


import io.summarizeit.backend.dto.AdminPermissions;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "UserOrganizations", description = "User Organizations", requiredMode = RequiredMode.REQUIRED)
public class UserOrganization {
    @Schema(name = "id", description = "Organization UUID", type = "UUID", example = "91b2999d-d327-4dc8-9956-2fadc0dc8778", requiredMode = RequiredMode.REQUIRED)
    private UUID id;

    @Schema(name = "name", description = "Name of the organization", type = "String", example = "John", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @Schema(name = "avatar", description = "URL of the organization's profile image", type = "String", example = "https://i.imgur.com/xapoQq3.jpeg", requiredMode = RequiredMode.REQUIRED)
    private String avatar;

    @Schema(name = "rootFolder", description = "UUID for the organization's root folder", type = "String", example = "9265k39d-d327-4dc8-9956-2fadc0dc8778", requiredMode = RequiredMode.REQUIRED)
    private UUID rootFolder;

    @Builder.Default
    @ArraySchema(schema = @Schema(implementation = AdminPermissions.class, requiredMode = RequiredMode.REQUIRED))
    private Set<AdminPermissions> adminPermissions = new HashSet<>();

    public void addAllAdminPermissions(Collection<? extends AdminPermissions> permissions){
        adminPermissions.addAll(permissions);
    }
}
