package org.parent.jira.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.graalvm.collections.Pair;
import org.parent.jira.exception.TokenNotFoundException;
import org.parent.jira.security.rsa.RSAKeyRecord;
import org.parent.jira.security.userconfiguration.UserDetailsImpl;
import org.parent.jira.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public JwtTokenUtils(RSAKeyRecord rsaKeyRecord, UserService userService) {
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
        this.userService = userService;
    }

    private final Function<Jwt, Pair<UserDetails, Collection<GrantedAuthority>>> tokenValidator = jwt -> {
        final var username = getUsername(jwt);
        final var userDetails = userDetails(username);

        Collection<GrantedAuthority> authorities = Optional.ofNullable(jwt.getClaim("scope"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(scopeStr -> Arrays.stream(scopeStr.split(" "))
                        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .collect(Collectors.<GrantedAuthority>toList()))
                .orElse(new ArrayList<>());

        authorities.addAll(userDetails.getAuthorities());

        if (!isTokenValid(jwt, userDetails)) throw new TokenNotFoundException("Invalid JWT token");
        return Pair.create(userDetails, authorities);
    };

    public void securityContextSetter(UserDetails userDetails, Collection<GrantedAuthority> authorities,
                                      HttpServletRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    public String getUsername(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails) {
        if (getIfTokenIsExpired(jwtToken)) return false;
        return getUsername(jwtToken).equals(userDetails.getUsername());
    }

    public UserDetails userDetails(String email) {
        return new UserDetailsImpl(userService.findEntityByEmail(email));
    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public boolean isSecurityContextEmpty() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    public Authentication getAuthenticationFromToken(String jwt) {
        final Jwt token = jwtDecoder.decode(jwt);
        final var pair = tokenValidator.apply(token);
        return new UsernamePasswordAuthenticationToken(pair.getLeft(), null, pair.getRight());
    }
}
