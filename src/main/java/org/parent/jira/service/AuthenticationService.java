package org.parent.jira.service;

import jakarta.servlet.http.HttpServletResponse;
import org.parent.jira.models.dto.request.SignInRequest;
import org.parent.jira.models.dto.request.SignUpRequest;
import org.parent.jira.models.dto.response.AuthResponse;

public interface AuthenticationService {
    AuthResponse getJwtTokensAfterAuthentication(SignInRequest sign, HttpServletResponse response);

    AuthResponse getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response);

    AuthResponse registerUser(SignUpRequest signUpRequestDto, HttpServletResponse httpServletResponse);

    void processOAuth2PostLogin(String email, String name, String providerId, HttpServletResponse response);
}
