package com.example.ridehailing.controller;

import com.example.ridehailing.annotation.RequiredRole;
import com.example.ridehailing.dto.DriverLocationDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.AuthService;
import com.example.ridehailing.service.DriverService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/drivers")
@Slf4j
public class DriverController {

    private final DriverService driverService;

    private final AuthService authService;

    public DriverController(DriverService driverService, AuthService authService) {
        this.driverService = driverService;
        this.authService = authService;
    }

    @PutMapping("/{userId}/location")
    @RequiredRole(UserType.DRIVER)
    public ResponseEntity<Void> updateLocation(@PathVariable Long userId, @RequestBody DriverLocationDto locationDto) {
        log.info("Received location update for user ID: {}", userId);
        driverService.updateLocation(userId, locationDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerDriver(@RequestHeader("Authorization") String authHeader) {
        log.info("Received driver registration request");
        UserDto userDto = authService.validateToken(authHeader);
        driverService.registerDriver(userDto.getId());
        log.info("Driver registered successfully for user ID: {}", userDto.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status/{userId}")
    @RequiredRole(UserType.DRIVER)
    public ResponseEntity<Void> updateDriverStatus(
            @PathVariable Long userId,
            @RequestBody String status) {
        log.info("Received status update for user ID: {} to {}", userId, status);
        driverService.updateDriverStatusUsingUserId(userId, status);
        log.info("Driver status updated successfully for user ID: {}", userId);
        return ResponseEntity.ok().build();
    }
}
