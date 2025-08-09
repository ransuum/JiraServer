package org.parent.jira.repository;

import org.parent.jira.models.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndRevokedIsFalse(String refreshToken);

    List<RefreshToken> findAllByExpiresAtBeforeOrRevokedIsTrue(Instant now);
}
