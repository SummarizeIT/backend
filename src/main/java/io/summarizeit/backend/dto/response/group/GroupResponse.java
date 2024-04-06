package io.summarizeit.backend.dto.response.group;

import java.util.List;
import java.util.UUID;

import io.summarizeit.backend.dto.GroupLeaderDto;
import io.summarizeit.backend.dto.response.AbstractBaseResponse;
import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class GroupResponse extends AbstractBaseResponse {
    @Schema(name = "parentId", description = "UUID of the group", type = "UUID", example = "120b2663-412a-4a98-8c7b-19115fd8a0b0")
    private UUID id;

    @Schema(name = "color", description = "Color code of the group", type = "String", example = "FF0000")
    private String color;

    @Schema(name = "name", description = "Name of the group", type = "String", example = "Sample Group")
    private String name;

    @Schema(name = "users", description = "Array of UUIDs representing users in the group", type = "array", example = "[\"6f0766f8-8580-4ec9-8674-3dcd72f8188b\",\"b743611b-8488-4694-9c91-49f94f2dfb24\"]")
    private List<UUID> users;

    @ArraySchema(schema = @Schema(implementation = GroupLeaderDto.class))
    private List<GroupLeaderDto> groupLeaders;

    public static GroupResponse convert(Group group) {
        return GroupResponse.builder().id(group.getId()).color(group.getColor()).name(group.getName())
                .users(group.getUsers().stream().map(User::getId).toList())
                .groupLeaders(group.getGroupLeaders().stream().map(GroupLeaderDto::convert).toList())
                .build();
    }
}
