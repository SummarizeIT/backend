package io.summarizeit.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.content.commons.annotations.ContentId;

import io.summarizeit.backend.event.UserListener;

@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners({UserListener.class})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PasswordResetToken passwordResetToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_folder_id")
    private Folder rootFolder;

    @ContentId
    @Column(name = "avatar_id", nullable = true)
    private UUID avatarId;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addGroup(Group group) {
        if (groups == null) {
            groups = new HashSet<>();
        }
        groups.add(group);
        group.getUsers().add(this);
    }

    public void removeGroup(Group group) {
        if (groups != null) {
            groups.remove(group);
            group.getUsers().remove(this);
        }
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        if (roles != null) {
            roles.remove(role);
            role.getUsers().remove(this);
        }
    }

    public void removeGroupUnsafe(Group group){
        if (groups != null)
            groups.remove(group);
    }

    public void removeRoleUnsafe(Role role){
        if (roles != null)
            roles.remove(role);
    }
}
