package io.summarizeit.backend.repository;

import io.summarizeit.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndRoles_Organization_Id(String email, UUID organizationId);

    @Query("select u from User u left join fetch u.roles left join fetch u.roles.organization where u.id = :id")
    List<User> findCustomAllRoles(@Param("id") UUID id);

    @Query("select u from User u left join fetch u.groups g where u in :users and g.organization.id = :organizationId")
    List<User> findCustomGroups(@Param("users") List<User> users, @Param("organizationId") UUID organizationId);

    @Query("select u from User u join fetch u.roles r where u in :users and r.organization.id = :organizationId and r.isDefault = false")
    List<User> findCustomRoles(@Param("users") List<User> users, @Param("organizationId") UUID organizationId);

    @Query("select u from User u join fetch u.groups g where u in :users and g.organization.id != :organizationId")
    List<User> findCustomGroupsNotOrg(@Param("users") List<User> users, @Param("organizationId") UUID organizationId);

    @Query("select u from User u join fetch u.roles r where u in :users and r.organization.id != :organizationId")
    List<User> findCustomRolesNotOrg(@Param("users") List<User> users, @Param("organizationId") UUID organizationId);

    List<User> findByIdInAndRoles_Organization_id(UUID[] ids, UUID organizationId);
}
