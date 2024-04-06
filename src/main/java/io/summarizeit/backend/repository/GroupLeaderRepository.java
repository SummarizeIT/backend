package io.summarizeit.backend.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.summarizeit.backend.entity.GroupLeader;

public interface GroupLeaderRepository extends JpaRepository<GroupLeader, UUID> {
    public List<GroupLeader> findByGroup_IdAndIdIn(UUID groupId, UUID[] ids);

    @Query("SELECT gl FROM GroupLeader gl JOIN FETCH gl.role WHERE gl.role.id IN ?1 AND gl.role.organization.id = ?2 AND gl.role.isDefault = ?3")
    public List<GroupLeader> findGroupRoles(UUID[] roleIds, UUID organizationId, Boolean isDefault);
}
