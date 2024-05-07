package io.summarizeit.backend.service.task;

import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.content.EntryContentStore;
import io.summarizeit.backend.service.TranscriptionService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TranscriptionTask {
    private final EntryContentStore entryContentStore;

    private final TranscriptionService transcriptionService;

    private final EntryRepository entryRepository;

    public Runnable getRunnable(Entry entry) {
        return new TranscriptionTaskRunnable(entry, entryContentStore, transcriptionService, entryRepository);
    }
}
