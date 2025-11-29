package com.example.ridehailing.service;

import com.example.ridehailing.dto.LoginResponseDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.exception.UnauthorizedException;
import com.example.ridehailing.model.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testLogin_Success() {
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        when(userService.verifyUser(any(UserRequestDto.class))).thenReturn(userDto);
        when(jwtService.generateToken(userDto)).thenReturn("mock.jwt.token");

        LoginResponseDto response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
    }

    @Test
    public void testLogin_Failure_InvalidCredentials() {
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(userService.verifyUser(any(UserRequestDto.class))).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    public void testGetUserFromToken_Success() {
        String authHeader = "Bearer valid.jwt.token";
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        when(jwtService.getUserFromToken("valid.jwt.token")).thenReturn(userDto);

        UserDto result = authService.validateToken(authHeader);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
    }

    @Test
    public void testGetUserFromToken_Failure_InvalidToken() {
        String token = "invalid.jwt.token";

        assertThrows(UnauthorizedException.class, () -> {
            authService.validateToken(token);
        });
    }
}
