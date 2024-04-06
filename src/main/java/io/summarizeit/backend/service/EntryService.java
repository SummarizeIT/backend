package io.summarizeit.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.content.commons.property.PropertyPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.ExtensionData;
import io.summarizeit.backend.dto.request.entry.MoveEntryRequest;
import io.summarizeit.backend.dto.request.entry.UpdateEntryRequest;
import io.summarizeit.backend.dto.request.entry.UploadEntryRequest;
import io.summarizeit.backend.dto.response.entry.EntryResponse;
import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.entity.EntryExtension;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.EntryExtensionRepository;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.content.EntryContentStore;
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

        @Transactional(readOnly = true)
        public EntryResponse getEntryById(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));

                List<ExtensionData> extensions = new ArrayList<>();
                entry.getExtensions().forEach(e -> extensions
                                .add(ExtensionData.builder().identifier(e.getIdentifier()).content(e.getContent())
                                                .build()));

                return EntryResponse.builder().title(entry.getTitle()).body(entry.getBody()).extensions(extensions)
                                .mediaType(entry.getMediaType()).url(Constants.getStaticFileUrl(entry.getMediaId()))
                                .createdOn(entry.getCreatedOn()).build();
        }

        @Transactional
        public void uploadEntry(UploadEntryRequest uploadEntryRequest, MultipartFile mediaFile) throws IOException {
                String extension = mediaFile.getOriginalFilename()
                                .substring(mediaFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
                String fileType = extension == "mp3" ? "AUDIO" : "VIDEO";
                Entry entry = entryRepository
                                .save(Entry.builder().title(uploadEntryRequest.getTitle()).mediaType(fileType).build());
                entryContentStore.setContent(entry, PropertyPath.from("media"), mediaFile.getInputStream());
                entryRepository.save(entry);
                //TODO: add queue to chatgpt-
        }

        @Transactional
        public void deleteEntry(UUID id) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService.get("entry") })));
                entryRepository.delete(entry);
        }

        @Transactional
        public void updateEntry(UUID id, UpdateEntryRequest updateEntryRequest) {
                Entry entry = entryRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                messageSourceService.get("not_found_with_param",
                                                                new String[] { messageSourceService
                                                                                .get("entry") })));
                entryExtensionRepository.deleteAll(entry.getExtensions());
                Set<EntryExtension> extensions = new HashSet<>();

                updateEntryRequest.getExtensions().forEach(extension -> extensions.add(EntryExtension.builder()
                                .identifier(extension.getIdentifier()).content(extension.getContent()).build()));

                entry.setTitle(updateEntryRequest.getTitle());
                entry.setBody(updateEntryRequest.getBody());
                entry.setExtensions(extensions);
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

                entry.setParentFolder(newParentFolder);
                entryRepository.save(entry);
        }
}
