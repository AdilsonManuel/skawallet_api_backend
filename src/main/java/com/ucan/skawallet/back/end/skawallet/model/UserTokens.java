package com.ucan.skawallet.back.end.skawallet.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
@Data
public class UserTokens
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkUserTokens;

    @ManyToOne
    @JoinColumn(name = "fk_users", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String accessToken;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
