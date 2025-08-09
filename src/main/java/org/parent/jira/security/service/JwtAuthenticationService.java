package org.parent.jira.security.service;

import jakarta.servlet.http.HttpServletResponse;
import org.parent.jira.models.dto.request.SignInRequest;
import org.parent.jira.models.entity.User;
import org.parent.jira.security.jwt.JwtHolder;
import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    JwtHolder createAuthObject(User users, HttpServletResponse response);

    void createAuthObjectForGoogle(User user, HttpServletResponse response);

    JwtHolder authenticateData(User user, Authentication authentication, HttpServletResponse response);

    Authentication authenticateCredentials(SignInRequest sign);

    JwtHolder createAuthObjectForVerification(User user, HttpServletResponse response);

    Authentication newAuthentication(User user);
}
