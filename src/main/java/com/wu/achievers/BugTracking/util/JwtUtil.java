package com.wu.achievers.BugTracking.util;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Use a static secret for demonstration; in production, store securely
    private final javax.crypto.SecretKey key =
            Keys.hmacShaKeyFor("YourSuperSecretKeyForJWTsMustBeAtLeast256BitsLong!".getBytes());

    private final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour in ms

    public String generateToken(String email, String role, Long userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiry = new Date(nowMillis + EXPIRATION_TIME);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(email) && claims.getExpiration().after(new Date());
    }

    public String extractRole(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = extractClaims(jwtToken);
        return claims.get("role", String.class);
    }

    public Long extractUserId(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = extractClaims(jwtToken);
        return claims.get("userId", Long.class);
    }

    // ----------------- NEW METHODS -----------------

    // Extract username (email) from token
    public String extractUsername(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return extractClaims(jwtToken).getSubject();
    }

    // Validate token (check signature + expiration)
    public boolean validateToken(String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Claims claims = extractClaims(jwtToken);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
