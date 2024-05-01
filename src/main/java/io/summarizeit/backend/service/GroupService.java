package io.summarizeit.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.summarizeit.backend.aspect.OrganizationPermission;
import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.dto.GroupLeaderDto;
import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.group.CreateGroupRequest;
import io.summarizeit.backend.dto.request.group.UpdateGroupRequest;
import io.summarizeit.backend.dto.response.group.GroupPaginationResponse;
import io.summarizeit.backend.dto.response.group.GroupResponse;
import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.GroupLeader;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import io.summarizeit.backend.entity.specification.criteria.PaginationCriteria;
import io.summarizeit.backend.exception.BadRequestException;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.util.Constants;
import io.summarizeit.backend.util.PageRequestBuilder;
import io.summarizeit.backend.repository.CustomGroupRepository;
import io.summarizeit.backend.repository.GroupLeaderRepository;
import io.summarizeit.backend.repository.GroupRepository;
import io.summarizeit.backend.repository.OrganizationRepository;
import io.summarizeit.backend.repository.RoleRepository;
import io.summarizeit.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {
        private final CustomGroupRepository customGroupRepository;

        private final GroupRepository groupRepository;

        private final UserRepository userRepository;

        private final GroupLeaderRepository groupLeaderRepository;

        private final OrganizationRepository organizationRepository;

        private final RoleRepository roleRepository;

        private final MessageSourceService messageSourceService;

        @Transactional(readOnly = true)
        public GroupPaginationResponse list(UUID organizationId, ListQuery listQuery) {
                GenericCriteria criteria = GenericCriteria.builder().ids(listQuery.getIds())
                                .search(listQuery.getSearch()).build();
                PaginationCriteria paginationCriteria = PaginationCriteria.builder().page(listQuery.getPage())
                                .size(Constants.PAGE_SIZE).build();

                Page<Group> page = customGroupRepository.findAll(criteria, organizationId,
                                PageRequestBuilder.build(paginationCriteria));

                return new GroupPaginationResponse(page, page.stream().map(GroupResponse::convert).toList());
        }

        @Transactional(readOnly = true)
        public GroupResponse getGroup(UUID id, UUID organizationId) {
                Group group = customGroupRepository.findOne(id, organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("group") })));

                return GroupResponse.convert(group);
        }

        @Transactional
        @OrganizationPermission(permissions = { AdminPermissions.ADMIN_GROUPS })
        public void updateOrganizationGroup(UUID id, UUID organizationId, UpdateGroupRequest updateGroupRequest) {
                if (groupRepository
                                .findByOrganizationIdAndNameAndIdNot(organizationId, updateGroupRequest.getName(), id)
                                .size() > 0)
                        throw new BadRequestException(messageSourceService.get("group_exists"));

                Group group = customGroupRepository.findOne(id, organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("group") })));

                List<User> users = userRepository.findByIdInAndRoles_Organization_id(updateGroupRequest.getUsers(),
                                organizationId);

                List<GroupLeaderDto> groupLeaderDtos = Arrays.asList(updateGroupRequest.getGroupLeaders());
                List<UUID> roleIds = groupLeaderDtos.stream().map(leader -> leader.getId()).toList();
                List<Role> roles = roleRepository.findByOrganizationIdAndIdIn(organizationId, roleIds);

                if (users.size() != updateGroupRequest.getUsers().length || roles.size() != roleIds.size())
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("group_or_user_or_role") }));

                Set<User> existingUsers = group.getUsers();
                existingUsers.forEach(user -> user.removeGroupUnsafe(group));
                users.forEach(user -> user.addGroup(group));
                users.addAll(existingUsers);
                userRepository.saveAll(users);

                List<GroupLeader> groupLeaders = new ArrayList<>();
                roles.stream().forEach(role -> {
                        GroupLeaderDto groupLeaderDto = groupLeaderDtos.stream()
                                        .filter(leaderDto -> leaderDto.getId().equals(role.getId())).findFirst().get();
                        groupLeaders.add(GroupLeader.builder().role(role).group(group)
                                        .changeExtensions(groupLeaderDto.getChangeExtensions()).build());
                });
                groupLeaderRepository
                                .deleteAllByIdInBatch(group.getGroupLeaders().stream().map(leader -> leader.getId())
                                                .toList());
                groupLeaderRepository.flush();
                groupLeaderRepository.saveAll(groupLeaders);

                group.setUsers(new HashSet<>(users));
                group.setColor(updateGroupRequest.getColor());
                group.setName(updateGroupRequest.getName());
                groupRepository.save(group);
        }

        @Transactional
        @OrganizationPermission(permissions = { AdminPermissions.ADMIN_GROUPS })
        public void createOrganizationGroup(UUID organizationId, CreateGroupRequest createGroupRequest) {
                if (groupRepository.findByOrganizationIdAndName(organizationId, createGroupRequest.getName())
                                .size() > 0)
                        throw new BadRequestException(messageSourceService.get("group_exists"));

                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("organization") })));

                List<User> users = userRepository.findByIdInAndRoles_Organization_id(createGroupRequest.getUsers(),
                                organizationId);

                List<GroupLeaderDto> groupLeaderDtos = Arrays.asList(createGroupRequest.getGroupLeaders());
                List<UUID> roleIds = groupLeaderDtos.stream().map(leader -> leader.getId()).toList();
                List<Role> roles = roleRepository.findByOrganizationIdAndIdIn(organizationId, roleIds);

                if (users.size() != createGroupRequest.getUsers().length || roles.size() != roleIds.size())
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("group_or_user_or_role") }));

                Group group = Group.builder().name(createGroupRequest.getName()).color(createGroupRequest.getColor())
                                .organization(organization).build();

                groupRepository.saveAndFlush(group);

                List<GroupLeader> groupLeaders = new ArrayList<>();
                roles.stream().forEach(role -> {
                        GroupLeaderDto groupLeaderDto = groupLeaderDtos.stream()
                                        .filter(leaderDto -> leaderDto.getId().equals(role.getId())).findFirst().get();
                        groupLeaders.add(GroupLeader.builder().role(role).group(group)
                                        .changeExtensions(groupLeaderDto.getChangeExtensions()).build());
                });
                List<GroupLeader> persistedGroupLeaders = groupLeaderRepository.saveAll(groupLeaders);

                users.forEach(u -> u.addGroup(group));
                userRepository.saveAll(users);

                group.setUsers(new HashSet<>(users));
                group.setGroupLeaders(new HashSet<>(persistedGroupLeaders));

                groupRepository.save(group);
        }

        @Transactional
        @OrganizationPermission(permissions = { AdminPermissions.ADMIN_GROUPS })
        public void deleteOrganizationGroup(UUID id, UUID organizationId) {
                Group group = groupRepository.findCustomUsersById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("group") })));

                group.getUsers().forEach(user -> user.removeGroupUnsafe(group));
                userRepository.saveAll(group.getUsers());
                groupRepository.delete(group);
        }
}
