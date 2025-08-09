package org.parent.jira.security.registrationchooser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.dto.request.SignUpRequest;
import org.parent.jira.models.entity.User;
import org.parent.jira.models.enums.Provider;
import org.parent.jira.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public final class EmailRegistration implements RegistrationChooser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registration(SignUpRequest signUpRequest) {
        log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequest);
        userRepository.findByEmail(signUpRequest.email()).ifPresent(_ -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
        });

        final var users = User.builder()
                .roles("ROLE_ADMIN")
                .email(signUpRequest.email())
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .provider(Provider.LOCAL)
                .lastLogin(Instant.now())
                .build();

        return userRepository.save(users);
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
