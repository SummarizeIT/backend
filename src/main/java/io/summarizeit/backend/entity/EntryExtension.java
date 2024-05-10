package io.summarizeit.backend.entity;

import java.util.UUID;

import org.apache.commons.lang3.builder.HashCodeExclude;

import io.summarizeit.backend.util.ObjectJsonStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry_extension")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String identifier;

    @Convert(converter = ObjectJsonStringConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private Object content;

    @HashCodeExclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;
}
