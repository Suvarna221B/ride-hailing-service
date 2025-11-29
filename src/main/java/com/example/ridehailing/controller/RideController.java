package com.example.ridehailing.controller;

import com.example.ridehailing.annotation.RequiredRole;
import com.example.ridehailing.dto.PaymentRequestDto;
import com.example.ridehailing.dto.RideRequestDto;
import com.example.ridehailing.dto.RideResponseDto;
import com.example.ridehailing.model.RideUpdateType;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/rides")
@Slf4j
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponseDto> createRide(@Valid @RequestBody RideRequestDto rideRequestDto) {
        log.info("Received ride creation request for user ID: {}", rideRequestDto.getUserId());
        RideResponseDto response = rideService.createRide(rideRequestDto);
        log.info("Ride created successfully with ID: {}", response.getRideId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/update")
    @RequiredRole(UserType.DRIVER)
    public ResponseEntity<Void> updateRide(@PathVariable Long rideId,
            @RequestParam Long driverId,
            @RequestParam RideUpdateType updateType) {
        log.info("Received update request for ride ID: {}, driver ID: {}, type: {}", rideId, driverId, updateType);
        rideService.updateRide(rideId, driverId, updateType);
        log.info("Ride {} updated successfully", rideId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponseDto> getRide(@PathVariable Long rideId) {
        log.info("Fetching ride with ID: {}", rideId);
        RideResponseDto ride = rideService.getRideById(rideId);
        log.info("Ride {} fetched successfully", rideId);
        return ResponseEntity.ok(ride);
    }

    @PostMapping("/{rideId}/payment")
    @RequiredRole(UserType.RIDER)
    public ResponseEntity<Void> processPayment(@PathVariable Long rideId,
            @RequestBody PaymentRequestDto paymentRequest) {
        log.info("Received payment request for ride ID: {}, amount: {}", rideId, paymentRequest.getAmount());
        rideService.processPayment(rideId, paymentRequest.getAmount(), paymentRequest.getPaymentMethod());
        log.info("Payment processed successfully for ride ID: {}", rideId);
        return ResponseEntity.ok().build();
    }
}
