package io.summarizeit.backend.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "folder")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @Column(nullable = false)
    private String name = "";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @Column(name = "public")
    private Boolean isPublic;

    @OneToOne(mappedBy = "rootFolder", fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "rootFolder", fetch = FetchType.LAZY)
    private Organization organization;

    @Builder.Default
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Entry> entries = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "folder_group",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"folder_id", "group_id"})
    )
    private Set<Group> groups;

    public void addEntry(Entry entry) {
        if (entries == null) {
            entries = new LinkedHashSet<>();
        }
        entries.add(entry);
        entry.setParentFolder(this);
    }
    
    public void removeEntry(Entry entry) {
        if (entries != null) {
            entries.remove(entry);
            entry.setParentFolder(null);
        }
    }

    public void clearGroups(){
        groups.clear();
    }
}
