package org.parent.jira.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.enums.TokenType;
import org.parent.jira.repository.RefreshTokenRepository;
import org.parent.jira.security.cookiemanagement.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerServiceImpl implements LogoutHandler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final var cookieUtil = new CookieUtils(request.getCookies());
        final var authCookie = cookieUtil.getRefreshTokenCookie();

        if (authCookie != null && !authCookie.startsWith(TokenType.Bearer.name())) return;

        final var refreshToken = Objects.requireNonNull(authCookie).substring(7);

        refreshTokenRepository.findByTokenAndRevokedIsFalse(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });

        cookieUtil.clearCookies(response);
    }
}
