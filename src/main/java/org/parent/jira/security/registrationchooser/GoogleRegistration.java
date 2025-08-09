package org.parent.jira.security.registrationchooser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.dto.request.SignUpRequest;
import org.parent.jira.models.entity.User;
import org.parent.jira.models.enums.Provider;
import org.parent.jira.repository.UserRepository;
import org.parent.jira.utils.PasswordUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public final class GoogleRegistration implements RegistrationChooser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registration(SignUpRequest signUpRequest) {
        log.info("Registering Google user with email: {}", signUpRequest.email());
        userRepository.findByEmail(signUpRequest.email()).ifPresent(_ -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this Google email already exists");
        });

        final var user = User.builder()
                .roles("ROLE_GOOGLE")
                .email(signUpRequest.email())
                .emailVerified(Boolean.TRUE)
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(PasswordUtils.generateSecure(16)))
                .provider(Provider.GOOGLE)
                .lastLogin(Instant.now())
                .providerId(signUpRequest.providerId())
                .build();

        return userRepository.save(user);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
