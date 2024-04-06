package io.summarizeit.backend.dto;

import java.util.UUID;

import io.summarizeit.backend.entity.GroupLeader;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoleLeader", description = "Properties for role group leader")
@Builder
public class GroupLeaderDto {
    @NotNull
    @Schema(name = "id", description = "UUID of role", type = "UUID", example = "41b2999d-d327-4dc8-9956-2fadc0dc8778")
    private UUID id;

    @NotNull
    @Schema(name = "changeExtensions", description = "Allow leader to change extensions", type = "Boolean", defaultValue = "true")
    private Boolean changeExtensions;

    public static GroupLeaderDto convert(GroupLeader leader) {
        return GroupLeaderDto.builder().id(leader.getId()).changeExtensions(leader.getChangeExtensions()).build();
    }
}
