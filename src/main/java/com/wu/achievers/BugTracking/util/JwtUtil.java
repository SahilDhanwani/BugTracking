package com.wu.achievers.BugTracking.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    // Use a static secret for demonstration; in production, store securely
    private final javax.crypto.SecretKey jwtSecretKey = Keys.hmacShaKeyFor("YourSuperSecretKeyForJWTsMustBeAtLeast256BitsLong!".getBytes());
    private final long jwtExpirationTimeMs = 60 * 60 * 1000; // 1 hour in ms

    public String generateToken(String email, String role, Long userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiry = new Date(nowMillis + jwtExpirationTimeMs);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(jwtSecretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(email) && claims.getExpiration().after(new Date());
    }

    public String extractRole(String token) {
        String jwtToken = token.substring(7);
        Claims claims = extractClaims(jwtToken);
        return claims.get("role", String.class);
    }

    public Long extractUserId(String token) {
        String jwtToken = token.substring(7);
        Claims claims = extractClaims(jwtToken);
        return claims.get("userId", Long.class);
    }
}
