package io.summarizeit.backend.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.service.MessageSourceService;
import io.summarizeit.backend.service.UserService;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final MessageSourceService messageSourceService;

    /**
     * Authenticate user.
     *
     * @param authentication Authentication
     */
    @Override
    @Transactional
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        System.out.println("test");
        User user = userService.findByEmail(authentication.getName());

        if (Objects.nonNull(authentication.getCredentials())) {
            boolean matches = passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword());
            if (!matches) {
                log.error("AuthenticationCredentialsNotFoundException occurred for {}", authentication.getName());
                throw new AuthenticationCredentialsNotFoundException(messageSourceService.get("bad_credentials"));
            }
        }

        UserDetails userDetails = userService.loadUserByEmail(authentication.getName());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }
}