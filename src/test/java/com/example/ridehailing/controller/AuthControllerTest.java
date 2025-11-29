package com.example.ridehailing.controller;

import com.example.ridehailing.config.SecurityConfig;
import com.example.ridehailing.dto.LoginResponseDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.exception.GlobalExceptionHandler;
import com.example.ridehailing.exception.UnauthorizedException;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogin_Success() throws Exception {
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .build();

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token("mock.jwt.token")
                .build();

        when(authService.login(any(UserRequestDto.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"));
    }

    @Test
    public void testLogin_Failure_InvalidCredentials() throws Exception {
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(authService.login(any(UserRequestDto.class)))
                .thenThrow(new UnauthorizedException("User not authorized"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authorized"));
    }

    @Test
    public void testValidateToken_Success() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        when(authService.validateToken("Bearer valid.jwt.token")).thenReturn(userDto);

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testValidateToken_Failure_InvalidToken() throws Exception {
        when(authService.validateToken("Bearer invalid.jwt.token"))
                .thenThrow(new UnauthorizedException("Invalid or expired token"));

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or expired token"));
    }
}
