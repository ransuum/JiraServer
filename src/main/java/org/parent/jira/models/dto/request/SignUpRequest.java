package org.parent.jira.models.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotBlank(message = "Username is blank")
        @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
        @Pattern(
                regexp = "^[a-zA-Z]{2,50}$",
                message = "Username must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
        )
        String username,

        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
        String email,

        @Valid
        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
        String password,

        String providerId
) {
}
