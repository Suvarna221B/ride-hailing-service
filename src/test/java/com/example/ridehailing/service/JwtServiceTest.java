package com.example.ridehailing.service;

import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.model.UserType;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    public void testGenerateToken() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        String token = jwtService.generateToken(userDto);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testValidateToken() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        String token = jwtService.generateToken(userDto);
        Claims claims = jwtService.validateToken(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("RIDER", claims.get("userType", String.class));
    }

    @Test
    public void testGetUserFromToken() {
        UserDto originalUser = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        String token = jwtService.generateToken(originalUser);
        UserDto extractedUser = jwtService.getUserFromToken(token);

        assertNotNull(extractedUser);
        assertEquals(originalUser.getId(), extractedUser.getId());
        assertEquals(originalUser.getUsername(), extractedUser.getUsername());
        assertEquals(originalUser.getUserType(), extractedUser.getUserType());
    }

    @Test
    public void testValidateInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtService.validateToken("invalid.token.here");
        });
    }
}
