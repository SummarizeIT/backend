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

import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.UserFilterSpecification;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserRepository {
    private final UserRepository userRepository;

    public Page<User> findAll(GenericCriteria criteria, UUID organizationId, Pageable pageable) {
        Page<User> users = userRepository.findAll(new UserFilterSpecification(organizationId, criteria), pageable);
        
        List<User> roleUsers = userRepository.findCustomRoles(users.toList(), organizationId);
        List<User> groupUsers = userRepository.findCustomGroups(users.toList(), organizationId);
        
        users.getContent().forEach(user -> {
            Set<Group> userGroups = groupUsers.stream()
                    .filter(groupUser -> groupUser.getId().equals(user.getId()))
                    .flatMap(groupUser -> groupUser.getGroups().stream())
                    .collect(Collectors.toSet());
            user.setGroups(userGroups);
            Set<Role> userRoles = roleUsers.stream()
                    .filter(roleUser -> roleUser.getId().equals(user.getId()))
                    .flatMap(roleUser -> roleUser.getRoles().stream())
                    .collect(Collectors.toSet());
            user.setRoles(userRoles);
        });

        return users;
    }

    public Optional<User> findOneNotOrg(UUID userId, UUID organizationId) {
        GenericCriteria criteria = GenericCriteria.builder().ids(new UUID[] { userId }).build();
        List<User> users = userRepository.findAll(new UserFilterSpecification(organizationId, criteria));
        if (users.isEmpty())
            return Optional.empty();
        
        List<User> roleUsers = userRepository.findCustomRolesNotOrg(users, organizationId);
        List<User> groupUsers = userRepository.findCustomRolesNotOrg(users, organizationId);
        
        users.forEach(user -> {
            Set<Group> userGroups = groupUsers.stream()
                    .filter(groupUser -> groupUser.getId().equals(user.getId()))
                    .flatMap(groupUser -> groupUser.getGroups().stream())
                    .collect(Collectors.toSet());
            user.setGroups(userGroups);
            Set<Role> userRoles = roleUsers.stream()
                    .filter(roleUser -> roleUser.getId().equals(user.getId()))
                    .flatMap(roleUser -> roleUser.getRoles().stream())
                    .collect(Collectors.toSet());
            user.setRoles(userRoles);
        });

        return Optional.ofNullable(users.get(0));
    }

    public Optional<User> findOne(UUID userId, UUID organizationId) {
        GenericCriteria criteria = GenericCriteria.builder().ids(new UUID[] { userId }).build();
        Page<User> user = this.findAll(criteria, organizationId, PageRequest.of(0, 1));
        if (!user.isEmpty()) {
            return Optional.of(user.getContent().get(0));
        } else {
            return Optional.empty();
        }
    }
}
