package io.summarizeit.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import io.summarizeit.backend.entity.Entry;


public interface EntryRepository extends JpaRepository<Entry, UUID> {
    public List<Entry> findByParentFolder_Id(UUID id);
}
