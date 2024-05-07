package io.summarizeit.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.content.commons.property.PropertyPath;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.ExtensionData;
import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.request.entry.MoveEntryRequest;
import io.summarizeit.backend.dto.request.entry.UpdateEntryRequest;
import io.summarizeit.backend.dto.request.entry.UploadEntryRequest;
import io.summarizeit.backend.dto.response.entry.EntryResponse;
import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.exception.FileSystemException;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.EntryExtensionRepository;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.content.EntryContentStore;
import io.summarizeit.backend.service.task.TranscriptionTask;
import io.summarizeit.backend.util.Constants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryService {
        private final EntryRepository entryRepository;

        private final MessageSourceService messageSourceService;

        private final EntryContentStore entryContentStore;

        private final FolderRepository folderRepository;

        private final EntryExtensionRepository entryExtensionRepository;

        private final FolderService folderService;

        private final UserService userService;

        private final PermissionService permissionService;

        private final SynchronousTaskService synchronousTaskService;

        private final TranscriptionTask transcriptionTask;

        @Transactional(readOnly = true)
        public EntryResponse getEntryById(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if (organization.isPresent())
                        if (!(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)
                                        || permissionService.isGroupMember(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                List<ExtensionData> extensions = new ArrayList<>();
                entry.getExtensions().forEach(e -> extensions
                                .add(ExtensionData.builder().identifier(e.getIdentifier()).content(e.getContent())
                                                .build()));
                return EntryResponse.builder().title(entry.getTitle()).body(entry.getBody()).extensions(extensions)
                                .mediaType(entry.getMediaType()).url(Constants.getStaticFileUrl(entry.getMediaId()))
                                .subtitles(Constants.getStaticFileUrl(entry.getSubtitleId()))
                                .isProcessing(entry.getTranscript().isEmpty() || entry.getTranscript() == null)
                                .createdOn(entry.getCreatedOn()).build();
        }

        @Transactional
        public void uploadEntry(UploadEntryRequest uploadEntryRequest, MultipartFile mediaFile) throws IOException {
                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(uploadEntryRequest.getParentFolderId());
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if (organization.isPresent())
                        if (!(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (entryRepository
                                .findByParentFolder_IdAndTitle(uploadEntryRequest.getParentFolderId(),
                                                uploadEntryRequest.getTitle())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                String extension = mediaFile.getOriginalFilename()
                                .substring(mediaFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
                String fileType = extension == "mp3" ? "AUDIO" : "VIDEO";
                // TODO: fix mediaType
                Entry entry = entryRepository
                                .save(Entry.builder().title(uploadEntryRequest.getTitle()).mediaType(fileType)
                                                .parentFolder(filesToRoot.get(0)).build());
                entryContentStore.setContent(entry, PropertyPath.from("media"), mediaFile.getInputStream());
                entryRepository.save(entry);
                synchronousTaskService.addTask(transcriptionTask.getRunnable(entry));
        }

        @Transactional
        public void deleteEntry(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if (organization.isPresent())
                        if (!(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                entryRepository.delete(entry);
        }

        @Transactional
        public void updateEntry(UUID id, UpdateEntryRequest updateEntryRequest) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("entry") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if (organization.isPresent())
                        if (!(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (!entry.getTitle().equals(updateEntryRequest.getTitle()) && entryRepository
                                .findByParentFolder_IdAndTitle(entry.getParentFolder().getId(),
                                                updateEntryRequest.getTitle())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                entryExtensionRepository.deleteAllInBatch(entry.getExtensions());

                entry.setTitle(updateEntryRequest.getTitle());
                entry.setBody(updateEntryRequest.getBody());
                entry = entryRepository.save(entry);
        }

        @Transactional
        public void moveEntry(UUID id, MoveEntryRequest moveEntryRequest) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("entry") })));

                if (moveEntryRequest.getDestinationFolderId() == entry.getParentFolder().getId())
                        return;

                Folder newParentFolder = folderRepository.findById(moveEntryRequest.getDestinationFolderId())
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("folder") })));

                List<Folder> filesToRootSource = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                List<Folder> filesToRootDestination = folderRepository.findFoldersToRoot(newParentFolder.getId());
                Optional<Organization> organizationSource = folderService.isOrganizationFolder(filesToRootSource);
                Optional<Organization> organizationDestination = folderService
                                .isOrganizationFolder(filesToRootDestination);
                List<UUID> groupIdsSource = folderService.getRecursiveGroups(filesToRootSource);
                List<UUID> groupIdsDestination = folderService.getRecursiveGroups(filesToRootDestination);
                User user = userService.getUser();

                if (organizationSource.isPresent() && organizationDestination.isPresent()
                                && organizationDestination.get().getId().equals(organizationSource.get().getId()))
                        if (!(permissionService.isMediaAdmin(organizationSource.get().getId())
                                        || (permissionService.isGroupLeader(user, groupIdsSource)
                                                        && permissionService.isGroupLeader(user, groupIdsDestination))))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (entryRepository
                                .findByParentFolder_IdAndTitle(moveEntryRequest.getDestinationFolderId(),
                                                entry.getTitle())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                entry.setParentFolder(newParentFolder);
                entryRepository.save(entry);
        }

        public void handleExtensionAction(UUID id, ExtensionRequest extensionRequest) {

        }
}
