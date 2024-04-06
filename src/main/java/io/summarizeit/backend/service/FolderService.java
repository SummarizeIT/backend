package io.summarizeit.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.summarizeit.backend.dto.request.folder.CreateFolderRequest;
import io.summarizeit.backend.dto.request.folder.MoveFolderRequest;
import io.summarizeit.backend.dto.request.folder.UpdateFolderPermissionsRequest;
import io.summarizeit.backend.dto.request.folder.UpdateFolderRequest;
import io.summarizeit.backend.dto.response.folder.DirectoryBreadcrumbsResponse;
import io.summarizeit.backend.dto.response.folder.FileSystemObject;
import io.summarizeit.backend.dto.response.folder.FolderObjectResponse;
import io.summarizeit.backend.dto.response.folder.FolderResponse;
import io.summarizeit.backend.dto.response.folder.MediaResponse;
import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
        private final FolderRepository folderRepository;

        private final EntryRepository entryRepository;

        private final MessageSourceService messageSourceService;

        private final GroupRepository groupRepository;

        @Transactional
        public void createFolder(CreateFolderRequest createFolderRequest) {
                Folder parentFolder = folderRepository.findById(createFolderRequest.getParentId())
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                Folder folder = Folder.builder().name(createFolderRequest.getName()).parentFolder(parentFolder)
                                .build();

                folderRepository.save(folder);
        }

        @Transactional
        public void deleteFolder(UUID id) {
                Folder folder = folderRepository.findById(id).orElseThrow(
                                () -> new NotFoundException(messageSourceService.get("not_found_with_param",
                                                new String[] { messageSourceService.get("folder") })));
                folderRepository.delete(folder);
        }

        @Transactional(readOnly = true)
        public FolderResponse getFolderById(UUID id) {
                List<Folder> folderGroups = folderRepository.findFolderGroups(id);
                if (folderGroups.size() == 0)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("folder") }));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(id);
                Collections.reverse(filesToRoot);
                List<Folder> nestedFolders = folderRepository.findNestedFolders(id);
                List<Entry> nestedEntries = entryRepository.findByParentFolder_Id(id);

                List<DirectoryBreadcrumbsResponse> breadcrumbs = new ArrayList<>();
                filesToRoot.forEach(folder -> breadcrumbs.add(DirectoryBreadcrumbsResponse.builder()
                                .name(folder.getName()).id(folder.getId()).isPublic(folder.getIsPublic()).build()));

                List<FileSystemObject> list = new ArrayList<>();
                nestedFolders.forEach(folder -> list.add(FolderObjectResponse.builder().name(folder.getName())
                                .id(folder.getId()).isPublic(folder.getIsPublic()).build()));
                nestedEntries.forEach(entry -> list
                                .add(MediaResponse.builder().name(entry.getTitle()).id(entry.getId()).build()));

                return FolderResponse.builder().list(list).pathFromRoot(breadcrumbs)
                                .groups(folderGroups.get(0).getGroups().stream().map(group -> group.getId()).toList())
                                .build();
        }

        @Transactional
        public void updateFolder(UUID id, UpdateFolderRequest updateFolderRequest) {
                Folder folder = folderRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));
                folder.setName(updateFolderRequest.getName());
                folderRepository.save(folder);
        }

        @Transactional
        public void updateFolderPermissions(UUID id, UpdateFolderPermissionsRequest updateFolderPermissionsRequest) {
                Folder folder = folderRepository.findParent(id).get(0);
                if (folder == null)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("folder") }));

                if (folder.getParentFolder().getOrganization() == null)
                        throw new UnsupportedOperationException(messageSourceService.get("only_root"));

                List<Group> newGroups = groupRepository.findByOrganizationIdAndIdIn(
                                folder.getParentFolder().getOrganization().getId(),
                                updateFolderPermissionsRequest.getGroups());
                if (newGroups.size() != updateFolderPermissionsRequest.getGroups().size())
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("group") }));

                folder.setGroups(new HashSet<>(newGroups));
                folder.setIsPublic(updateFolderPermissionsRequest.isPublic());
        }

        @Transactional
        public void moveFolder(UUID id, MoveFolderRequest moveFolderRequest) {
                Folder folder = folderRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                if (moveFolderRequest.getDestinationFolderId() == folder.getParentFolder().getId())
                        return;

                Folder newParentFolder = folderRepository.findById(moveFolderRequest.getDestinationFolderId())
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                folder.setParentFolder(newParentFolder);
                folder.clearGroups();
                folderRepository.save(folder);
        }
}
