package io.summarizeit.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.summarizeit.backend.entity.Invite;

public interface InviteRepository extends JpaRepository<Invite, UUID> {
    public List<Invite> findByUserEmail(String userEmail);

    public Optional<Invite> findByUserEmailAndOrganization_Id(String userEmail, UUID organizationId);
}
