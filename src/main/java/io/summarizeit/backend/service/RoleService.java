package io.summarizeit.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.summarizeit.backend.aspect.OrganizationPermission;
import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.role.CreateRoleRequest;
import io.summarizeit.backend.dto.request.role.UpdateRoleRequest;
import io.summarizeit.backend.dto.response.role.RolePaginationResponse;
import io.summarizeit.backend.dto.response.role.RoleResponse;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import io.summarizeit.backend.entity.specification.criteria.PaginationCriteria;
import io.summarizeit.backend.exception.BadRequestException;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.util.Constants;
import io.summarizeit.backend.util.PageRequestBuilder;
import io.summarizeit.backend.repository.CustomRoleRepository;
import io.summarizeit.backend.repository.OrganizationRepository;
import io.summarizeit.backend.repository.RoleRepository;
import io.summarizeit.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
        private final CustomRoleRepository customRoleRepository;

        private final UserRepository userRepository;

        private final OrganizationRepository organizationRepository;

        private final RoleRepository roleRepository;

        private final MessageSourceService messageSourceService;

        @Transactional(readOnly = true)
        @OrganizationPermission(permissions = {AdminPermissions.ADMIN_ROLES, AdminPermissions.ADMIN_GROUPS, AdminPermissions.ADMIN_USERS})
        public RolePaginationResponse list(UUID organizationId, ListQuery listQuery) {
                GenericCriteria criteria = GenericCriteria.builder().ids(listQuery.getIds())
                                .search(listQuery.getSearch()).build();
                PaginationCriteria paginationCriteria = PaginationCriteria.builder().page(listQuery.getPage())
                                .size(Constants.PAGE_SIZE).build();

                Page<Role> page = customRoleRepository.findAll(criteria, organizationId,
                                PageRequestBuilder.build(paginationCriteria));

                return new RolePaginationResponse(page, page.stream().map(RoleResponse::convert).toList());
        }

        @Transactional(readOnly = true)
        @OrganizationPermission(permissions = {AdminPermissions.ADMIN_ROLES, AdminPermissions.ADMIN_GROUPS, AdminPermissions.ADMIN_USERS})
        public RoleResponse getRole(UUID id, UUID organizationId) {
                Role role = customRoleRepository.findOne(id, organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("group") })));

                return RoleResponse.convert(role);
        }

        @Transactional
        @OrganizationPermission(permissions = {AdminPermissions.ADMIN_ROLES})
        public void updateOrganizationRole(UUID id, UUID organizationId, UpdateRoleRequest updateRoleRequest) {
                if (roleRepository.findByOrganizationIdAndNameAndIdNot(organizationId, updateRoleRequest.getName(), id)
                                .size() > 0)
                        throw new BadRequestException(messageSourceService.get("group_exists"));

                Role role = customRoleRepository.findOne(id, organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("role") })));

                if (role.getName() == Constants.ADMIN_ROLE_NAME)
                        throw new BadRequestException(messageSourceService.get("admin-role-error"));

                List<User> users = userRepository.findByIdInAndRoles_Organization_id(updateRoleRequest.getUsers(),
                                organizationId);

                if (users.size() != updateRoleRequest.getUsers().length)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("group_or_user_or_role") }));

                Set<User> existingUsers = role.getUsers();
                existingUsers.forEach(user -> user.removeRoleUnsafe(role));
                users.forEach(user -> user.addRole(role));
                users.addAll(existingUsers);
                userRepository.saveAll(users);

                List<AdminPermissions> adminPerms = updateRoleRequest.getAdminPermissions().stream()
                                .map(str -> AdminPermissions.valueOf(str)).toList();
                role.setAdminPermissions(adminPerms);
                role.setUsers(new HashSet<>(users));
                role.setName(updateRoleRequest.getName());
                roleRepository.save(role);
        }

        @Transactional
        @OrganizationPermission(permissions = {AdminPermissions.ADMIN_ROLES})
        public void createOrganizationRole(UUID organizationId, CreateRoleRequest createRoleRequest) {
                if (roleRepository.findByOrganizationIdAndName(organizationId, createRoleRequest.getName()).size() > 0)
                        throw new BadRequestException(messageSourceService.get("group_exists"));

                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("organization") })));

                List<User> users = userRepository.findByIdInAndRoles_Organization_id(createRoleRequest.getUsers(),
                                organizationId);

                if (users.size() != createRoleRequest.getUsers().length)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("user") }));

                List<AdminPermissions> adminPerms = createRoleRequest.getAdminPermissions().stream()
                                .map(str -> AdminPermissions.valueOf(str)).toList();

                Role role = Role.builder().name(createRoleRequest.getName())
                                .adminPermissions(adminPerms)
                                .organization(organization)
                                .isDefault(false)
                                .build();

                roleRepository.saveAndFlush(role);

                users.forEach(u -> u.addRole(role));
                userRepository.saveAll(users);

                role.setUsers(new HashSet<>(users));
                roleRepository.save(role);
        }

        @Transactional
        @OrganizationPermission(permissions = {AdminPermissions.ADMIN_ROLES})
        public void deleteOrganizationRole(UUID id, UUID organizationId) {
                Role role = roleRepository.findRoleUsersById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("role") })));

                role.getUsers().forEach(user -> user.removeRoleUnsafe(role));
                userRepository.saveAll(role.getUsers());
                roleRepository.delete(role);
        }
}
