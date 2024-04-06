package io.summarizeit.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.summarizeit.backend.entity.Folder;


public interface FolderRepository extends JpaRepository<Folder, UUID> {
    @Query(value = "WITH RECURSIVE recfolder AS( SELECT * FROM folder WHERE id = ?1 UNION ALL SELECT f.* FROM folder f JOIN recfolder rf ON f.id = rf.parent_folder_id) SELECT * FROM recfolder; ", nativeQuery = true)
    public List<Folder> findFoldersToRoot(UUID id);

    @Query(value = "SELECT f FROM Folder f where f.parentFolder.id = ?1")
    public List<Folder> findNestedFolders(UUID id);

    @Query(value = "SELECT f FROM Folder f LEFT JOIN f.parentFolder where f.id = ?1")
    public List<Folder> findParent(UUID id);

    @Query("SELECT f FROM Folder f LEFT JOIN FETCH f.groups where f.id = ?1")
    public List<Folder> findFolderGroups(UUID id);

    @Query(value = "SELECT f FROM Folder f LEFT JOIN FETCH f.entries where f.id = ?1")
    public List<Folder> findNestedEntries(UUID id);
}
