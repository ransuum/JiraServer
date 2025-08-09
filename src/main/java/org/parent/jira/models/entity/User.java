package org.parent.jira.models.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.parent.jira.models.enums.Provider;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;

    @Column(nullable = false, name = "EMAIL_ID", unique = true)
    private String email;

    @Column(nullable = false, name = "PASSWORD")
    private String password;

    @Column(nullable = false, name = "ROLES")
    private String roles;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = Boolean.FALSE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private Instant lastLogin;
}
