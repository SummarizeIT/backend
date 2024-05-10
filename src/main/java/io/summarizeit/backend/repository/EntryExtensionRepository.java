package io.summarizeit.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.EntryExtension;

public interface EntryExtensionRepository extends JpaRepository<EntryExtension, UUID>{
    Optional<EntryExtension> findByEntryAndIdentifier(Entry entry, String identifier);
}
