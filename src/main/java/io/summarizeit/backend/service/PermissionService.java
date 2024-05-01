package io.summarizeit.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.GroupLeader;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.repository.GroupLeaderRepository;
import io.summarizeit.backend.repository.GroupRepository;
import io.summarizeit.backend.repository.RoleRepository;
import io.summarizeit.backend.util.Constants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final RoleRepository roleRepository;

    private final GroupLeaderRepository groupLeaderRepository;

    private final GroupRepository groupRepository;

    private final UserService userService;

    public Boolean isAdmin(User user, UUID organizationId) {
        return roleRepository.findByOrganizationIdAndUsers_IdAndName(organizationId, user.getId(), Constants.ADMIN_ROLE_NAME).size() != 0;
    }

    public List<AdminPermissions> getAdminPermissions(User user, UUID organizationId) {
        List<Role> roles = roleRepository.findByOrganizationIdAndUsers_Id(organizationId, user.getId());

        Set<AdminPermissions> perms = new HashSet<>();
        roles.forEach(role -> perms.addAll(role.getAdminPermissions()));
        return perms.stream().toList();
    }

    public Boolean isMediaAdmin(UUID organizationId) {
        User user = userService.getUser();
        return isAdmin(user, organizationId)
                && getAdminPermissions(user, organizationId).contains(AdminPermissions.ADMIN_MEDIA);
    }

    private List<GroupLeader> getGroupLeadership(User user, List<UUID> groupsIds) {
        return groupLeaderRepository.findByGroup_IdInAndRole_Users_Id(groupsIds, user.getId());
    }

    private List<Group> getGroups(User user, List<UUID> groupIds) {
        return groupRepository.findByIdInAndUsers_Id(groupIds, user.getId());
    }

    public Boolean isGroupLeader(User user, List<UUID> groupIds) {
        if (groupIds.size() == 0)
            return false;
        return getGroupLeadership(user, groupIds).size() == groupIds.size();
    }

    public Boolean isGroupMember(User user, List<UUID> groupIds) {
        if (groupIds.size() == 0)
            return true;
        return getGroups(user, groupIds).size() == groupIds.size();
    }
}
