package io.summarizeit.backend.entity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry")
@Data
@EntityListeners({EntryListener.class})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @Column(columnDefinition = "text")
    private String body = "";

    @Builder.Default
    @Column(columnDefinition = "text")
    private String transcript = "";

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column
    @CreationTimestamp
    private Instant createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "entry", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<EntryExtension> extensions;

    @ContentId
    @Column(name = "media_id")
    private UUID mediaId;

    @Column(name = "media_type")
    private String mediaType;

    @ContentId
    @Column(name = "subtitle_id")
    private UUID subtitleId;
}