package com.example.ridehailing.service;

import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.model.User;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .userType(UserType.RIDER)
                .build();

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setUserType(UserType.RIDER);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto createdUser = userService.createUser(requestDto);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals(UserType.RIDER, createdUser.getUserType());
        assertNotNull(createdUser.getId()); // ID should be present in DTO
    }

    @Test
    public void testVerifyUser_Success() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .build();

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setUserType(UserType.RIDER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        UserDto verifiedUser = userService.verifyUser(requestDto);

        assertNotNull(verifiedUser);
        assertEquals("testuser", verifiedUser.getUsername());
    }

    @Test
    public void testVerifyUser_InvalidPassword() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        UserDto result = userService.verifyUser(userRequestDto);

        assertNull(result);
    }

    @Test
    public void testGetUserEntityById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testGetUserEntityById_NotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> userService.getUserEntityById(userId));
    }
}
