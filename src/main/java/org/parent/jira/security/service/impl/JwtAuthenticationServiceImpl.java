package org.parent.jira.security.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.parent.jira.models.dto.request.SignInRequest;
import org.parent.jira.models.entity.RefreshToken;
import org.parent.jira.models.entity.User;
import org.parent.jira.repository.RefreshTokenRepository;
import org.parent.jira.security.jwt.JwtHolder;
import org.parent.jira.security.jwt.JwtTokenGenerator;
import org.parent.jira.security.service.JwtAuthenticationService;
import org.parent.jira.security.userconfiguration.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 25;

    @Override
    public JwtHolder createAuthObject(User user, HttpServletResponse response) {
        final var auth = createAuthentication(user);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public void createAuthObjectForGoogle(User user, HttpServletResponse response) {
        final var auth = createAuthentication(user);
        generateAndSaveTokens(user, auth, response, false);
    }

    @Override
    public JwtHolder authenticateData(User user, Authentication authentication, HttpServletResponse response) {
        return generateAndSaveTokens(user, authentication, response, true);
    }

    @Override
    public Authentication authenticateCredentials(SignInRequest sign) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    @Override
    public JwtHolder createAuthObjectForVerification(User user, HttpServletResponse response) {
        final var auth = createAndSetAuthentication(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public Authentication newAuthentication(User user) {
        return createAndSetAuthentication(user, null);
    }

    private JwtHolder generateAndSaveTokens(User user, Authentication auth, HttpServletResponse response, boolean includeAccessToken) {
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(auth);
        final var accessToken = includeAccessToken ? jwtTokenGenerator.generateAccessToken(auth) : null;

        jwtTokenGenerator.createRefreshTokenCookie(response, refreshToken);
        saveRefreshToken(user, refreshToken);

        return includeAccessToken ? new JwtHolder(refreshToken, accessToken) : null;
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build());
    }

    private Authentication createAuthentication(User user) {
        return jwtTokenGenerator.createAuthenticationObject(user);
    }

    private Authentication createAndSetAuthentication(User user, String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), password, new UserDetailsImpl(user).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }
}
