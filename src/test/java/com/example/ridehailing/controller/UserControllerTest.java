package com.example.ridehailing.controller;

import com.example.ridehailing.config.SecurityConfig;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterUser() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .userType(UserType.RIDER)
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .build();

        UserDto responseDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .userType(UserType.RIDER)
                .build();

        when(userService.verifyUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(userService.verifyUser(any(UserRequestDto.class))).thenReturn(null);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }
}
