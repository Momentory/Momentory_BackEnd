package com.example.momentory.global.security;

import com.example.momentory.domain.user.entity.User;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    // === 액세스 토큰 발급 ===
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("userId", user.getId())
                .claim("role", "USER")
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTokenExpiration)))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // === 리프레시 토큰 발급 ===
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("userId", user.getId())
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTokenExpiration)))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // === 토큰 파싱 ===
    public Claims extractClaims(String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new GeneralException(ErrorStatus.JWT_MALFORMED);
        }
    }

    // === 유효성 검증 ===
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // === userId 추출 ===
    public Long extractUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        Object rawUserId = claims.get("userId");
        if (rawUserId instanceof Integer) return ((Integer) rawUserId).longValue();
        if (rawUserId instanceof Long) return (Long) rawUserId;
        if (rawUserId instanceof String) return Long.parseLong((String) rawUserId);
        throw new GeneralException(ErrorStatus.JWT_MALFORMED);
    }

    // === Authentication 생성 (SecurityContext용) ===
    public Authentication getAuthentication(String token) {
        Long userId = extractUserIdFromToken(token);
        return new UsernamePasswordAuthenticationToken(
                userId, // principal
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
