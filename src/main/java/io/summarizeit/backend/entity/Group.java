package io.summarizeit.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group", indexes = {
        @Index(columnList = "name", name = "idx_group_name")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color", nullable = false)
    private String color;

    @Builder.Default
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private Set<User> users = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GroupLeader> groupLeaders = new LinkedHashSet<>();

    public void setGroupLeaders(Set<GroupLeader> newGroupLeaders) {
        this.groupLeaders.clear();
        if (newGroupLeaders != null)
            this.groupLeaders.addAll(newGroupLeaders);
    }

    public void setUsers(Set<User> newUsers) {
        this.users.clear();
        if (newUsers != null)
            this.users.addAll(newUsers);
    }

    public void addUser(User user) {
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(user);
        user.getGroups().add(this);
    }

    public void removeUser(User user) {
        if (users != null) {
            users.remove(user);
            user.getGroups().remove(this);
        }
    }
}