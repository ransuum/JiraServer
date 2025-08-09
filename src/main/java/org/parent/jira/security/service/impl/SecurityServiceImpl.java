package org.parent.jira.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.enums.Roles;
import org.parent.jira.security.service.SecurityService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    @Override
    public String getCurrentUserEmail() {
        return getAuthenticationOrThrow().getName();
    }

    @Override
    public boolean hasAnyRole(List<String> roles) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roles::contains);
    }

    @Override
    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    @Override
    public String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getPermissionsFromRoles(String roles) {
        final var roleList = Arrays.stream(roles.split(","))
                .map(String::trim)
                .toList();
        log.info("[SecurityService:getPermissionsFromRoles] Roles: {}", roleList);

        final var authorities = Roles.getPermissionsForRoles(roleList);
        return authorities.stream()
                .map(Enum::name)
                .collect(Collectors.joining(" "));
    }

    @Override
    public Authentication getAuthenticationOrThrow() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new AuthenticationCredentialsNotFoundException("No authenticated user found in SecurityContext");

        return authentication;
    }

    private Collection<SimpleGrantedAuthority> getCurrentUserRoles() {
        final var authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        return authentication.map(authentication1 ->
                        authentication1.getAuthorities()
                                .stream()
                                .filter(SimpleGrantedAuthority.class::isInstance)
                                .map(SimpleGrantedAuthority.class::cast)
                                .toList())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No authenticated user found"));
    }
}
