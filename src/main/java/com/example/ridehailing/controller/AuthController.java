package com.example.ridehailing.controller;

import com.example.ridehailing.dto.LoginResponseDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserRequestDto userRequestDto) {
        log.info("Received login request for username: {}", userRequestDto.getUsername());
        LoginResponseDto response = authService.login(userRequestDto);
        log.info("Login successful for username: {}", userRequestDto.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDto> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Received token validation request");
        UserDto userDto = authService.validateToken(authHeader);
        log.info("Token validated successfully for user ID: {}", userDto.getId());
        return ResponseEntity.ok(userDto);
    }
}
