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
import io.summarizeit.backend.entity.GroupLeader;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.GroupFilterSpecification;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomGroupRepository {
    private final GroupRepository groupRepository;
    
    public Page<Group> findAll(GenericCriteria criteria, UUID organizationId, Pageable pageable) {
        Page<Group> groups = groupRepository.findAll(new GroupFilterSpecification(criteria, organizationId), pageable);

        List<Group> userGroups = groupRepository.findCustomUsers(groups.toList());
        List<Group> groupLeaderGroups = groupRepository.findCustomGroupLeaders(groups.toList());

        groups.getContent().forEach(group -> {
            Set<User> users = userGroups.stream()
                    .filter(userGroup -> userGroup.getId().equals(group.getId()))
                    .flatMap(userGroup -> userGroup.getUsers().stream())
                    .collect(Collectors.toSet());
            group.setUsers(users);
            Set<GroupLeader> groupLeaders = groupLeaderGroups.stream()
                    .filter(groupLeader -> groupLeader.getId().equals(group.getId()))
                    .flatMap(roleUser -> roleUser.getGroupLeaders().stream())
                    .collect(Collectors.toSet());
            group.setGroupLeaders(groupLeaders);
        });

        return groups;
    }

    public Optional<Group> findOne(UUID groupId, UUID organizationId) {
        GenericCriteria criteria = GenericCriteria.builder().ids(new UUID[] { groupId }).build();
        Page<Group> group = this.findAll(criteria, organizationId, PageRequest.of(0, 1));
        if (!group.isEmpty()) {
            return Optional.of(group.getContent().get(0));
        } else {
            return Optional.empty();
        }
    }
    
}
