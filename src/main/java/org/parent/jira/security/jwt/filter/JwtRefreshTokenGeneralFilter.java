package org.parent.jira.security.jwt.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.repository.RefreshTokenRepository;
import org.parent.jira.security.cookiemanagement.CookieUtils;
import org.parent.jira.security.jwt.JwtTokenUtils;
import org.parent.jira.security.rsa.RSAKeyRecord;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtRefreshTokenGeneralFilter extends JwtGeneralFilter {
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtRefreshTokenGeneralFilter(RSAKeyRecord rsaKeyRecord,
                                        JwtTokenUtils jwtTokenUtils,
                                        RefreshTokenRepository refreshTokenRepository) {
        super(rsaKeyRecord, jwtTokenUtils);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected String extractToken(HttpServletRequest request) {
        return new CookieUtils(request.getCookies()).getRefreshTokenCookie();
    }

    @Override
    protected boolean isTokenValidInContext(Jwt jwt) {
        return refreshTokenRepository.findByTokenAndRevokedIsFalse(jwt.getTokenValue())
                .isPresent();
    }
}
