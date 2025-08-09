package org.parent.jira.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parent.jira.models.dto.request.SignInRequest;
import org.parent.jira.models.dto.request.SignUpRequest;
import org.parent.jira.models.dto.response.AuthResponse;
import org.parent.jira.service.AuthenticationService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody SignInRequest signInDto, HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.getJwtTokensAfterAuthentication(signInDto, response));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUpUser(@Valid @RequestBody SignUpRequest signUpRequestDto,
                                             BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for user:{}", signUpRequestDto.username());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}", errorMessage);
            return ResponseEntity.status(400).body(errorMessage);
        }
        return ResponseEntity.status(201).body(authenticationService.registerUser(signUpRequestDto, httpServletResponse));
    }

    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> getAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authenticationService.getAccessTokenUsingRefreshToken(refreshTokenFromCookie, httpServletResponse));
    }
}
