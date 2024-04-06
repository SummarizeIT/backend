package io.summarizeit.backend.security;

import io.summarizeit.backend.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public final class JwtUserDetails implements UserDetails {
    private String id;

    private String email;

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * JwtUserDetails constructor.
     *
     * @param id          String
     * @param email       String
     * @param password    String
     * @param authorities Collection<? extends GrantedAuthority>
     */
    private JwtUserDetails(final String id, final String email, final String password) {
        this.id = id;
        this.email = email;
        this.username = email;
        this.password = password;
    }

    /**
     * Create JwtUserDetails from User.
     *
     * @param user User
     * @return JwtUserDetails
     */
    public static JwtUserDetails create(final User user) {
        return new JwtUserDetails(user.getId().toString(), user.getEmail(), user.getPassword());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
