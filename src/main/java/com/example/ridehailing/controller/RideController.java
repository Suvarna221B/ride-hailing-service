package com.example.ridehailing.controller;

import com.example.ridehailing.dto.PaymentRequestDto;
import com.example.ridehailing.dto.RideRequestDto;
import com.example.ridehailing.dto.RideResponseDto;
import com.example.ridehailing.model.RideUpdateType;
import com.example.ridehailing.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponseDto> createRide(@Valid @RequestBody RideRequestDto rideRequestDto) {
        RideResponseDto response = rideService.createRide(rideRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideId}/update")
    public ResponseEntity<Void> updateRide(@PathVariable Long rideId,
            @RequestParam Long driverId,
            @RequestParam RideUpdateType updateType) {
        rideService.updateRide(rideId, driverId, updateType);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{rideId}/payment")
    public ResponseEntity<Void> processPayment(@PathVariable Long rideId,
            @RequestBody PaymentRequestDto paymentRequest) {
        rideService.processPayment(rideId, paymentRequest.getAmount());
        return ResponseEntity.ok().build();
    }
}
