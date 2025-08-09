package org.parent.jira.models.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.parent.jira.models.enums.TokenType;

@Builder
public record AuthResponse(@JsonProperty("access_token") String accessToken,
                           @JsonProperty("access_token_expiry") int accessTokenExpiry,
                           @JsonProperty("token_type") TokenType tokenType,
                           @JsonProperty("user_name") String username) {
}
