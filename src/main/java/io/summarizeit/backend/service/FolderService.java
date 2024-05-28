package io.summarizeit.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
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
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.exception.BadRequestException;
import io.summarizeit.backend.exception.FileSystemException;
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

        private final PermissionService permissionService;

        private final UserService userService;

        @Transactional
        public void createFolder(CreateFolderRequest createFolderRequest) {
                Folder parentFolder = folderRepository.findById(createFolderRequest.getParentId())
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(parentFolder.getId());
                Optional<User> potentialUser = isUserFolder(filesToRoot);
                Optional<Organization> organization = isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (folderRepository
                                .findByParentFolderAndName(parentFolder, createFolderRequest.getName())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                Folder folder = Folder.builder().name(createFolderRequest.getName()).parentFolder(parentFolder)
                                .build();

                folderRepository.save(folder);
        }

        @Transactional
        public void deleteFolder(UUID id) {
                Folder folder = folderRepository.findById(id).orElseThrow(
                                () -> new NotFoundException(messageSourceService.get("not_found_with_param",
                                                new String[] { messageSourceService.get("folder") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(folder.getId());
                Optional<User> potentialUser = isUserFolder(filesToRoot);
                Optional<Organization> organization = isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));

                List<Folder> childrenFolders = folderRepository.findChildrenFolders(id);
                entryRepository.deleteByParentFolder(childrenFolders);
                folderRepository.deleteAllInBatch(childrenFolders);
        }

        @Transactional(readOnly = true)
        public FolderResponse getFolderById(UUID id) {
                List<Folder> folderGroups = folderRepository.findFolderGroups(id);
                if (folderGroups.size() == 0)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("folder") }));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(id);
                Optional<User> potentialUser = isUserFolder(filesToRoot);
                Optional<Organization> organization = isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                Boolean isAdmin = false;
                if (organization.isPresent()) {
                        isAdmin = permissionService.isMediaAdmin(organization.get().getId());
                        if (!(isAdmin || permissionService.isGroupMember(user, groupIds)
                                        || permissionService.isGroupLeader(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));
                } else if (potentialUser.isPresent() && potentialUser.get() != user) {
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));
                }

                Collections.reverse(filesToRoot);
                List<Folder> nestedFolders;
                if (organization.isPresent() && !isAdmin)
                        nestedFolders = folderRepository.findFilteredNestedFolders(id, user.getId());
                else
                        nestedFolders = folderRepository.findNestedFolders(id);

                List<Entry> nestedEntries = entryRepository.findByParentFolder_Id(id);

                List<DirectoryBreadcrumbsResponse> breadcrumbs = new ArrayList<>();
                filesToRoot.forEach(folder -> breadcrumbs.add(DirectoryBreadcrumbsResponse.builder()
                                .name(folder.getName()).id(folder.getId())
                                /* .isPublic(folder.getIsPublic()) */.build()));

                List<FileSystemObject> list = new ArrayList<>();
                nestedFolders.forEach(folder -> list.add(FolderObjectResponse.builder().name(folder.getName())
                                .id(folder.getId())/* .isPublic(folder.getIsPublic()) */.build()));
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

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(folder.getId());
                Optional<User> potentialUser = isUserFolder(filesToRoot);
                Optional<Organization> organization = isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (!folder.getName().equals(updateFolderRequest.getName()) && folderRepository
                                .findByParentFolderAndName(folder.getParentFolder(), updateFolderRequest.getName())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                folder.setName(updateFolderRequest.getName());
                folderRepository.save(folder);
        }

        @Transactional
        public void updateFolderPermissions(UUID id, UpdateFolderPermissionsRequest updateFolderPermissionsRequest) {
                Folder folder = folderRepository.findWithParent(id).get(0);
                if (folder == null)
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("folder") }));

                if (folder.getParentFolder().getOrganization() == null)
                        throw new UnsupportedOperationException(messageSourceService.get("only_organization_root"));

                User user = userService.getUser();

                if (!(permissionService.isMediaAdmin(folder.getParentFolder().getOrganization().getId())
                                || permissionService.isGroupLeader(user,
                                                folder.getGroups().stream().map(group -> group.getId()).toList())))
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));

                List<Group> newGroups = groupRepository.findByOrganizationIdAndIdIn(
                                folder.getParentFolder().getOrganization().getId(),
                                updateFolderPermissionsRequest.getGroups());
                if (newGroups.size() != updateFolderPermissionsRequest.getGroups().size())
                        throw new NotFoundException(messageSourceService.get("not_found_with_param",
                                        new String[] { messageSourceService.get("group") }));

                folder.setGroups(new HashSet<>(newGroups));
                // folder.setIsPublic(updateFolderPermissionsRequest.isPublic());
                folderRepository.save(folder);
        }

        @Transactional
        public void moveFolder(UUID id, MoveFolderRequest moveFolderRequest) {
                Folder folder = folderRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                if (moveFolderRequest.getDestinationFolderId() == folder.getParentFolder().getId())
                        throw new BadRequestException(messageSourceService.get("duplicate-folder-name"));

                Folder newParentFolder = folderRepository.findById(moveFolderRequest.getDestinationFolderId())
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                List<Folder> filesToRootSource = folderRepository.findFoldersToRoot(folder.getId());
                List<Folder> filesToRootDestination = folderRepository.findFoldersToRoot(newParentFolder.getId());
                Optional<Organization> organizationSource = isOrganizationFolder(filesToRootSource);
                Optional<Organization> organizationDestination = isOrganizationFolder(filesToRootDestination);
                Optional<User> userSource = isUserFolder(filesToRootSource);
                Optional<User> userDestination = isUserFolder(filesToRootDestination);
                List<UUID> groupIdsSource = getRecursiveGroups(filesToRootSource);
                List<UUID> groupIdsDestination = getRecursiveGroups(filesToRootDestination);
                User user = userService.getUser();

                if (organizationSource.isPresent() && organizationDestination.isPresent()
                                && organizationDestination.get().getId().equals(organizationSource.get().getId()))
                        if (!(permissionService.isMediaAdmin(organizationSource.get().getId())
                                        || (permissionService.isGroupLeader(user, groupIdsSource)
                                                        && permissionService.isGroupLeader(user, groupIdsDestination))))
                                throw new AccessDeniedException("permission-error");
                if (!(userSource.isPresent() && userDestination.isPresent() && userDestination.get() == userSource.get()
                                && userSource.get() == user))
                        throw new AccessDeniedException("permission-error");

                if (folderRepository
                                .findByParentFolderAndName(newParentFolder, folder.getName())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                folder.setParentFolder(newParentFolder);
                folder.clearGroups();
                folderRepository.save(folder);
        }

        public Optional<Organization> isOrganizationFolder(List<Folder> pathToRoot) {
                return Optional.ofNullable(pathToRoot.get(pathToRoot.size() - 1).getOrganization());
        }

        public Optional<User> isUserFolder(List<Folder> pathToRoot) {
                return Optional.ofNullable(pathToRoot.get(pathToRoot.size() - 1).getUser());
        }

        public List<UUID> getRecursiveGroups(List<Folder> pathToRoot) {
                Set<UUID> groupIds = new HashSet<>();
                pathToRoot.forEach(folder -> groupIds
                                .addAll(folder.getGroups().stream().map(group -> group.getId()).toList()));
                return groupIds.stream().toList();
        }
}
