package io.summarizeit.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.content.commons.property.PropertyPath;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.ExtensionData;
import io.summarizeit.backend.dto.request.entry.ExtensionRequest;
import io.summarizeit.backend.dto.request.entry.MoveEntryRequest;
import io.summarizeit.backend.dto.request.entry.UpdateEntryRequest;
import io.summarizeit.backend.dto.request.entry.UploadEntryRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.dto.response.entry.EntryResponse;
import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.EntryExtension;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.exception.BadRequestException;
import io.summarizeit.backend.exception.FileSystemException;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.EntryExtensionRepository;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.content.EntryContentStore;
import io.summarizeit.backend.service.task.PreprocessTask;
import io.summarizeit.backend.util.Constants;
import io.summarizeit.backend.util.ExtensionContext;
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

        private final PreprocessTask preprocessTask;

        private final ExtensionRegistrarService extensionRegistrarService;

        @Transactional(readOnly = true)
        public EntryResponse getEntryById(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                Authentication authentication = userService.getAuthentication();
                if (authentication.isAuthenticated() && authentication.getAuthorities().stream()
                                .filter(auth -> auth.equals(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))).toList()
                                .size() == 0) {
                        List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                        Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                        Optional<User> potentialUser = folderService.isUserFolder(filesToRoot);
                        List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                        User user = userService.getUser();

                        if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)
                                        || permissionService.isGroupMember(user, groupIds)))
                                        || (potentialUser.isPresent() && potentialUser.get() != user))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                } else {
                        if (entry.getIsPublic() != true)
                                throw new AccessDeniedException(
                                                "Full authentication is required to access this resource");
                }

                List<ExtensionData> extensions = new ArrayList<>();
                entry.getExtensions().forEach(e -> extensions
                                .add(ExtensionData.builder().identifier(e.getIdentifier()).content(e.getContent())
                                                .build()));
                return EntryResponse.builder().title(entry.getTitle()).extensions(extensions)
                                .url(Constants.getStaticFileUrl(entry.getMediaId()))
                                .transcript(entry.getTranscript())
                                .isPublic(entry.getIsPublic())
                                .isProcessing(entry.getTranscript().isEmpty() || entry.getTranscript() == null)
                                .createdOn(entry.getCreatedOn()).build();
        }

        @Transactional
        public void uploadEntry(UploadEntryRequest uploadEntryRequest, MultipartFile mediaFile) throws IOException {
                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(uploadEntryRequest.getParentFolderId());
                Optional<User> potentialUser = folderService.isUserFolder(filesToRoot);
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
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
                synchronousTaskService.addTask(preprocessTask.getRunnable(entry));
        }

        @Transactional
        public void deleteEntry(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                Optional<User> potentialUser = folderService.isUserFolder(filesToRoot);
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
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
                Optional<User> potentialUser = folderService.isUserFolder(filesToRoot);
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if ((organization.isPresent() && !(permissionService.isMediaAdmin(organization.get().getId())
                                || permissionService.isGroupLeader(user, groupIds)))
                                || (potentialUser.isPresent() && potentialUser.get() != user))
                        throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (!entry.getTitle().equals(updateEntryRequest.getTitle()) && entryRepository
                                .findByParentFolder_IdAndTitle(entry.getParentFolder().getId(),
                                                updateEntryRequest.getTitle())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                //entryExtensionRepository.deleteAllInBatch(entry.getExtensions());
                //entryExtensionRepository.flush();

                List<String> validExtensions = extensionRegistrarService.getExtensionIdentifiers();
                final Entry finalEntry = entry;
                List<EntryExtension> extensions = updateEntryRequest.getExtensions().stream()
                                .filter(data -> validExtensions.contains(data.getIdentifier()))
                                .map(data -> EntryExtension.builder().identifier(data.getIdentifier())
                                                .content(data.getContent()).entry(finalEntry).build())
                                .toList();

                extensions = entryExtensionRepository.saveAll(extensions);
                entry.clearExtensions();
                extensions.forEach(entry::addExtension);
                entry.setTitle(updateEntryRequest.getTitle());
                entry.setIsPublic(updateEntryRequest.getIsPublic());
                entryRepository.save(entry);
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
                Optional<User> userSource = folderService.isUserFolder(filesToRootSource);
                Optional<User> userDestination = folderService.isUserFolder(filesToRootDestination);
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
                if (!(userSource.isPresent() && userDestination.isPresent() && userDestination.get() == userSource.get()
                                && userSource.get() == user))
                        throw new AccessDeniedException("permission-error");

                if (entryRepository
                                .findByParentFolder_IdAndTitle(moveEntryRequest.getDestinationFolderId(),
                                                entry.getTitle())
                                .size() != 0)
                        throw new FileSystemException(
                                        messageSourceService.get("duplicate-folder-name"));

                entry.setParentFolder(newParentFolder);
                entryRepository.save(entry);
        }

        @Transactional
        public void handleExtensionPayload(UUID id, ExtensionRequest extensionRequest) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                Optional<EntryExtension> ext = entryExtensionRepository.findByEntryAndIdentifier(entry,
                                extensionRequest.getIdentifier());

                List<Folder> filesToRoot = folderRepository.findFoldersToRoot(entry.getParentFolder().getId());
                Optional<Organization> organization = folderService.isOrganizationFolder(filesToRoot);
                List<UUID> groupIds = folderService.getRecursiveGroups(filesToRoot);
                User user = userService.getUser();

                if (organization.isPresent())
                        if (!(permissionService.isMediaAdmin(organization.get().getId())
                                        || permissionService.isGroupLeader(user, groupIds)
                                        || permissionService.isGroupMember(user, groupIds)))
                                throw new AccessDeniedException(messageSourceService.get("permission-error"));

                if (entry.getTranscript() == null || entry.getTranscript().equals(""))
                        throw new BadRequestException("entry-still-processing");

                ExtensionResponse response = extensionRegistrarService.call(extensionRequest.getIdentifier(),
                                extensionRequest.getCommand(), extensionRequest.getPayload(),
                                ExtensionContext.convert(entry));
                EntryExtension newExtension = entryExtensionRepository
                                .save(EntryExtension.builder().identifier(extensionRequest.getIdentifier())
                                                .content(response).entry(entry).build());
                entry.addExtension(newExtension);
                ext.ifPresent(exten -> entry.removeExtension(exten));
                entryRepository.save(entry);
        }
}
