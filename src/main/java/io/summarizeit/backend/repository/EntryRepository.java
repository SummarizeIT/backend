package io.summarizeit.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.Folder;


public interface EntryRepository extends JpaRepository<Entry, UUID> {
    public List<Entry> findByParentFolder_Id(UUID id);

    public List<Entry> findByParentFolder_IdAndTitle(UUID id, String title);

    @Modifying
    @Query("delete from Entry e where e.parentFolder in :folders")
    public void deleteByParentFolder(@Param("folders") List<Folder> folders);
}
