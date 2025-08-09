package org.parent.jira.security.jwt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.entity.User;
import org.parent.jira.security.service.SecurityService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final JwtEncoder jwtEncoder;
    private final SecurityService securityService;

    public Authentication createAuthenticationObject(User users) {
        final var roles = users.getRoles();

        final String[] roleArray = roles.split(",");
        final List<GrantedAuthority> authorities = Arrays.stream(roleArray)
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword(), authorities);
    }

    public String generateAccessToken(Authentication authentication) {
        final var jti = UUID.randomUUID().toString();
        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());
        final var roles = securityService.getRolesOfUser(authentication);
        final var permissions = securityService.getPermissionsFromRoles(roles);

        final var claims = JwtClaimsSet.builder()
                .id(jti)
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        final int REFRESH_TOKEN_EXPIRE_SECONDS = 25 * 24 * 60 * 60;

        final var cookieBuilder = "refreshToken=" + refreshToken +
                "; Max-Age=" + REFRESH_TOKEN_EXPIRE_SECONDS +
                "; Path=/" +
                "; HttpOnly" +
                "; Secure" +
                "; SameSite=None";

        response.setHeader("Set-Cookie", cookieBuilder);
    }

    public String generateRefreshToken(Authentication authentication) {
        final var jti = UUID.randomUUID().toString();
        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        final var claims = JwtClaimsSet.builder()
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .id(jti)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
