package io.summarizeit.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import io.summarizeit.backend.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    @Query("SELECT r FROM Role r WHERE r.organization.id = ?1 AND r.id in ?2 and r.isDefault = false and r.name != 'Admin'")
    public List<Role> findByOrganizationIdAndIdIn(UUID organizationId, List<UUID> ids);

    public Optional<Role> findByOrganizationIdAndIsDefault(UUID organizationId, Boolean isDefault);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r IN ?1")
    public List<Role> findRoleUsers(List<Role> roles);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.id = ?1 AND r.name != 'Admin' AND r.isDefault = false")
    public Optional<Role> findRoleUsersById(UUID id);

    public List<Role> findByOrganizationIdAndName(UUID organizationId, String name);

    public List<Role> findByOrganizationIdAndNameAndIdNot(UUID organizationId, String name, UUID id);
}
