package com.example.ridehailing.controller;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import com.example.ridehailing.service.FareCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fare")
public class FareController {

    private final FareCalculationService fareCalculationService;

    public FareController(FareCalculationService fareCalculationService) {
        this.fareCalculationService = fareCalculationService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<FareResponseDto> calculateFare(@RequestBody FareRequestDto request) {
        FareResponseDto response = fareCalculationService.calculateFare(request);
        return ResponseEntity.ok(response);
    }
}
