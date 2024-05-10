package io.summarizeit.backend.util;

import io.summarizeit.backend.entity.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ExtensionContext {
    private Entry entry;

    public static ExtensionContext convert(Entry entry) {
        return ExtensionContext.builder()
                .entry(Entry.builder().title(entry.getTitle()).createdOn(entry.getCreatedOn()).id(entry.getId())
                        .extensions(entry.getExtensions()).parentFolder(entry.getParentFolder())
                        .transcript(entry.getTranscript()).build())
                .build();
    }
}
