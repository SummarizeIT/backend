package io.summarizeit.backend.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.content.commons.annotations.ContentId;

import io.summarizeit.backend.event.EntryListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "entry")
@EntityListeners({ EntryListener.class })
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @Column(columnDefinition = "text")
    private String transcript = "";

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @HashCodeExclude
    @Builder.Default
    @OneToMany(mappedBy = "entry", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntryExtension> extensions = new HashSet<>();

    @ContentId
    @Column(name = "media_id")
    private UUID mediaId;

    @Column(name = "media_type")
    private String mediaType;

    @ContentId
    @Column(name = "subtitle_id")
    private UUID subtitleId;

    @Builder.Default
    @Column(name = "public", nullable = false)
    private Boolean isPublic = false;

    public void addExtension(EntryExtension entryExtension) {
        if (this.extensions == null) {
            this.extensions = new HashSet<>();
        }
        extensions.add(entryExtension);
    }

    public void removeExtension(EntryExtension entryExtension) {
        if (this.extensions == null) {
            return;
        }
        extensions.remove(entryExtension);
    }

    public void clearExtensions() {
        if (this.extensions == null) {
            return;
        }
        extensions.clear();
    }
}
