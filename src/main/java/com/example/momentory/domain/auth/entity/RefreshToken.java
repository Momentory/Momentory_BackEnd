package com.example.momentory.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public static RefreshToken of(Long userId, String refreshToken, LocalDateTime expiryDate) {
        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiryDate(expiryDate)
                .build();
    }
}
