package com.hzsca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long expireSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expire-seconds}") long expireSeconds
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret is required (set env JWT_SECRET)");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("security.jwt.secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    public String generateToken(long userId, String role) {
        return generateToken(userId, role, null);
    }

    public String generateToken(long userId, String role, String principalType) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expireSeconds);
        var builder = Jwts.builder()
                .issuer(issuer)
                .subject(Long.toString(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("role", role)
                .signWith(key);
        if (principalType != null && !principalType.isBlank()) {
            builder = builder.claim("ptype", principalType);
        }
        return builder.compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }
}
