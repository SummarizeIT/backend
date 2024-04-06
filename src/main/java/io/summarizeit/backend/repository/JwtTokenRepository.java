package io.summarizeit.backend.repository;

import org.springframework.data.repository.CrudRepository;

import io.summarizeit.backend.entity.cache.JwtToken;

import java.util.Optional;
import java.util.UUID;

public interface JwtTokenRepository extends CrudRepository<JwtToken, UUID> {
    Optional<JwtToken> findByTokenOrRefreshToken(String token, String refreshToken);

    Optional<JwtToken> findByUserIdAndRefreshToken(UUID id, String refreshToken);
}
