package io.summarizeit.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import io.summarizeit.backend.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    @Query(value = "WITH RECURSIVE recfolder AS( SELECT * FROM folder WHERE id = ?1 UNION ALL SELECT f.* FROM folder f JOIN recfolder rf ON f.id = rf.parent_folder_id) SELECT * FROM recfolder; ", nativeQuery = true)
    public List<Folder> findFoldersToRoot(UUID id);

    @Query(value = "SELECT f FROM Folder f where f.parentFolder.id = ?1")
    public List<Folder> findNestedFolders(UUID id);

    @Query(value = "SELECT f FROM Folder f LEFT JOIN f.parentFolder where f.id = ?1")
    public List<Folder> findWithParent(UUID id);

    @Query("SELECT f FROM Folder f LEFT JOIN FETCH f.groups where f.id = ?1")
    public List<Folder> findFolderGroups(UUID id);

    @Query(value = "SELECT f FROM Folder f LEFT JOIN FETCH f.entries where f.id = ?1")
    public List<Folder> findNestedEntries(UUID id);

    public List<Folder> findByParentFolderAndName(Folder parentFolder, String name);

    @Query(value = "SELECT f.* FROM folder f LEFT JOIN folder_group fg ON fg.folder_id = f.id LEFT JOIN \"group\" g ON g.id = fg.group_id WHERE f.parent_folder_id = :parentId AND( EXISTS ( SELECT 1 FROM group_user gu JOIN \"user\" u ON u.id = gu.user_id WHERE gu.group_id = g.id AND u.id = :userId) OR EXISTS ( SELECT 1 FROM group_leader gl JOIN \"role\" r ON r.id = gl.role_id JOIN role_user ru ON r.id = ru.role_id JOIN \"user\" ul ON ul.id = ru.user_id WHERE gl.group_id = g.id AND ul.id = :userId ) OR g.id IS NULL )", nativeQuery = true)
    public List<Folder> findFilteredNestedFolders(@Param("parentId") UUID parentId, @Param("userId") UUID userId);

    @Query(value = "with recursive recfolder as ( select * from folder where id = :id union all select f.* from folder f join recfolder rf on f.parent_folder_id = rf.id) select * from recfolder;", nativeQuery = true )
    public List<Folder> findChildrenFolders(@Param("parentId") UUID id);
}
