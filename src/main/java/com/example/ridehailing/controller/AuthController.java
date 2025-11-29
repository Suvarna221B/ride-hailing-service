package com.example.ridehailing.controller;

import com.example.ridehailing.dto.LoginResponseDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.dto.UserRequestDto;
import com.example.ridehailing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserRequestDto userRequestDto) {
        LoginResponseDto response = authService.login(userRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDto> validateToken(@RequestHeader("Authorization") String authHeader) {
        UserDto userDto = authService.validateToken(authHeader);
        return ResponseEntity.ok(userDto);
    }
}
