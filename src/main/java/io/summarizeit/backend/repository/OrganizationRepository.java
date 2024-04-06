package io.summarizeit.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import io.summarizeit.backend.entity.Organization;


public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    public List<Organization> findByRoles_Users_Id(UUID userId);
}
