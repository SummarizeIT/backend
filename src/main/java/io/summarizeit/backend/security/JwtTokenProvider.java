package io.summarizeit.backend.security;

import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.cache.JwtToken;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.service.JwtTokenService;
import io.summarizeit.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

import javax.crypto.SecretKey;

import static io.summarizeit.backend.util.Constants.TOKEN_HEADER;
import static io.summarizeit.backend.util.Constants.TOKEN_TYPE;

@Component
@Slf4j
public class JwtTokenProvider {
    private final UserService userService;

    private final String appSecret;

    @Getter
    private final Long tokenExpiresIn;

    @Getter
    private Long refreshTokenExpiresIn;

    private final Long rememberMeTokenExpiresIn;

    private final JwtTokenService jwtTokenService;

    private final HttpServletRequest httpServletRequest;

    public JwtTokenProvider(
            @Value("${app.secret}") final String appSecret,
            @Value("${app.jwt.token.expires-in}") final Long tokenExpiresIn,
            @Value("${app.jwt.refresh-token.expires-in}") final Long refreshTokenExpiresIn,
            @Value("${app.jwt.remember-me.expires-in}") final Long rememberMeTokenExpiresIn,
            final UserService userService,
            final JwtTokenService jwtTokenService,
            final HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.appSecret = appSecret;
        this.tokenExpiresIn = tokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.rememberMeTokenExpiresIn = rememberMeTokenExpiresIn;
        this.jwtTokenService = jwtTokenService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Generate token by user ID.
     *
     * @param id      String
     * @param expires Long
     * @return String
     */
    public String generateTokenByUserId(final String id, final Long expires) {
        String token = Jwts.builder()
                .subject(id)
                .issuedAt(new Date())
                .expiration(getExpireDate(expires))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
        log.trace("Token is added to the local cache for userID: {}, ttl: {}", id, expires);

        return token;
    }

    /**
     * Generate JWT token by user ID.
     *
     * @param id String
     * @return String
     */
    public String generateJwt(final String id) {
        return generateTokenByUserId(id, tokenExpiresIn);
    }

    /**
     * Generate refresh token by user ID.
     *
     * @param id String
     * @return String
     */
    public String generateRefresh(final String id) {
        return generateTokenByUserId(id, refreshTokenExpiresIn);
    }

    /**
     * Get JwtUserDetails from authentication.
     *
     * @param authentication Authentication
     * @return JwtUserDetails
     */
    public JwtUserDetails getPrincipal(final Authentication authentication) {
        return userService.getPrincipal(authentication);
    }

    /**
     * Get user ID from token.
     *
     * @param token String
     * @return String
     */
    public String getUserIdFromToken(final String token) {
        Claims claims = parseToken(token).getPayload();

        return claims.getSubject();
    }

    /**
     * Get user from token.
     *
     * @param token String
     * @return User
     */
    public User getUserFromToken(final String token) {
        try {
            return userService.findById(getUserIdFromToken(token));
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    public boolean validateToken(final String token) {
        return validateToken(token, true);
    }

    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    public boolean validateToken(final String token, final boolean isHttp) {
        parseToken(token);
        try {
            JwtToken jwtToken = jwtTokenService.findByTokenOrRefreshToken(token);
            if (isHttp && !httpServletRequest.getHeader("User-agent").equals(jwtToken.getUserAgent())) {
                log.error("[JWT] User-agent is not matched");
                return false;
            }
        } catch (NotFoundException e) {
            log.error("[JWT] Token could not found in Redis");
            return false;
        }

        return !isTokenExpired(token);
    }

    /**
     * Validate token.
     *
     * @param token              String
     * @param httpServletRequest HttpServletRequest
     * @return boolean
     */
    public boolean validateToken(final String token, final HttpServletRequest httpServletRequest) {
        try {
            boolean isTokenValid = validateToken(token);
            if (!isTokenValid) {
                log.error("[JWT] Token could not found in local cache");
                httpServletRequest.setAttribute("notfound", "Token is not found in cache");
            }
            return isTokenValid;
        } catch (UnsupportedJwtException e) {
            log.error("[JWT] Unsupported JWT token!");
            httpServletRequest.setAttribute("unsupported", "Unsupported JWT token!");
        } catch (MalformedJwtException e) {
            log.error("[JWT] Invalid JWT token!");
            httpServletRequest.setAttribute("invalid", "Invalid JWT token!");
        } catch (ExpiredJwtException e) {
            log.error("[JWT] Expired JWT token!");
            httpServletRequest.setAttribute("expired", "Expired JWT token!");
        } catch (IllegalArgumentException e) {
            log.error("[JWT] Jwt claims string is empty");
            httpServletRequest.setAttribute("illegal", "JWT claims string is empty.");
        }

        return false;
    }

    /**
     * Set jwt refresh token for remember me option.
     */
    public void setRememberMe() {
        this.refreshTokenExpiresIn = rememberMeTokenExpiresIn;
    }

    /**
     * Extract jwt from bearer string.
     *
     * @param bearer String
     * @return String value of bearer token or null
     */
    public String extractJwtFromBearerString(final String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith(String.format("%s ", TOKEN_TYPE))) {
            return bearer.substring(TOKEN_TYPE.length() + 1);
        }

        return null;
    }

    /**
     * Extract jwt from request.
     *
     * @param request HttpServletRequest object to get Authorization header
     * @return String value of bearer token or null
     */
    public String extractJwtFromRequest(final HttpServletRequest request) {
        return extractJwtFromBearerString(request.getHeader(TOKEN_HEADER));
    }

    /**
     * Parsing token.
     *
     * @param token String jwt token to parse
     * @return Jws object
     */
    private Jws<Claims> parseToken(final String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
    }

    /**
     * Check token is expired or not.
     *
     * @param token String jwt token to get expiration date
     * @return True or False
     */
    private boolean isTokenExpired(final String token) {
        return parseToken(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * Get expire date.
     *
     * @return Date object
     */
    private Date getExpireDate(final Long expires) {
        return new Date(new Date().getTime() + expires);
    }

    /**
     * Get signing key.
     *
     * @return Key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(appSecret.getBytes());
    }
}
