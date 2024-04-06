package io.summarizeit.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import io.summarizeit.backend.entity.Group;


public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    public List<Group> findByOrganizationIdAndIdIn(UUID organizationId, List<UUID> ids);

    public List<Group> findByOrganizationIdAndName(UUID organizationId, String name);

    public List<Group> findByOrganizationIdAndNameAndIdNot(UUID organizationId, String name, UUID id);

    @Query("select g from Group g join fetch g.users where g in :groups")
    List<Group> findCustomUsers(@Param("groups") List<Group> groups);

    @Query("select g from Group g left join fetch g.users where g.id = :id")
    Optional<Group> findCustomUsersById(@Param("id") UUID id);

    @Query("select g from Group g join fetch g.groupLeaders where g in :groups")
    List<Group> findCustomGroupLeaders(@Param("groups") List<Group> groups);
}