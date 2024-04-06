package io.summarizeit.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.RoleFilterSpecification;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomRoleRepository {
    private final RoleRepository roleRepository;

    public Page<Role> findAll(GenericCriteria criteria, UUID organizationId, Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(new RoleFilterSpecification(criteria, organizationId), pageable);
        
        List<Role> roleUsers = roleRepository.findRoleUsers(roles.toList());
        
        roles.getContent().forEach(role -> {
            Set<User> users = roleUsers.stream()
                    .filter(roleUser -> roleUser.getId().equals(role.getId()))
                    .flatMap(roleUser -> roleUser.getUsers().stream())
                    .collect(Collectors.toSet());
            role.setUsers(users);
        });

        return roles;
    }

    public Optional<Role> findOne(UUID roleId, UUID organizationId) {
        GenericCriteria criteria = GenericCriteria.builder().ids(new UUID[] { roleId }).build();
        Page<Role> user = this.findAll(criteria, organizationId, PageRequest.of(0, 1));
        if (!user.isEmpty()) {
            return Optional.of(user.getContent().get(0));
        } else {
            return Optional.empty();
        }
    }
}
