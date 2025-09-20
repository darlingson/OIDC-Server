package com.darlingson.OIDC_Server.services;

import com.darlingson.OIDC_Server.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String generateAccessToken(User user, String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getFullName());
        claims.put("roles", user.getRole().getName().name());
        claims.put("scope", scope);

        // Use user ID as subject
        return buildToken(claims, user.getId().toString(), jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        // Use user ID as subject for refresh token too
        return buildToken(new HashMap<>(), user.getId().toString(), jwtExpiration * 24 * 7);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Get email from claims
        return claims.get("email", String.class);
    }

    public Integer extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        // Subject is now the user ID
        return Integer.parseInt(subject);
    }

    public Set<String> extractScopes(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String scope = claims.get("scope", String.class);
            if (scope != null) {
                return Arrays.stream(scope.split(" ")).collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }
}