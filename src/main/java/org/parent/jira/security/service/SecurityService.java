package org.parent.jira.security.service;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface SecurityService {
    String getCurrentUserEmail();

    boolean hasAnyRole(List<String> roles);

    boolean hasRole(String role);

    String getRolesOfUser(Authentication authentication);

    String getPermissionsFromRoles(String roles);

    Authentication getAuthenticationOrThrow();
}
