package io.summarizeit.backend.event;

import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.repository.content.EntryContentStore;
import jakarta.persistence.PostRemove;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EntryListener {
    private final EntryContentStore entryContentStore;
    
    @PostRemove
    public void removeMedia(Entry entry){
        entryContentStore.unsetContent(entry);
    }
}
