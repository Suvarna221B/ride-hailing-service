package com.example.ridehailing.service;

import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.model.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getId());
        claims.put("username", userDto.getUsername());
        claims.put("userType", userDto.getUserType().name());

        return Jwts.builder()
                .claims(claims)
                .subject(userDto.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UserDto getUserFromToken(String token) {
        Claims claims = validateToken(token);

        return UserDto.builder()
                .id(claims.get("userId", Long.class))
                .username(claims.get("username", String.class))
                .userType(UserType.valueOf(claims.get("userType", String.class)))
                .build();
    }
}
