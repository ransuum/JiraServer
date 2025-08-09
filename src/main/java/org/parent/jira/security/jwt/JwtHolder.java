package org.parent.jira.security.jwt;

public record JwtHolder(String refreshToken, String accessToken) { }