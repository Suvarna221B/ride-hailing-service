package com.example.ridehailing.service;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FareCalculationServiceTest {

    @InjectMocks
    private FareCalculationService fareCalculationService;

    @Test
    public void testCalculateFare_WithoutTime() {
        ReflectionTestUtils.setField(fareCalculationService, "farePerKm", 14.0);
        ReflectionTestUtils.setField(fareCalculationService, "expectedSpeedKmph", 20.0);

        // Coordinates for approximately 10 km distance
        FareRequestDto request = FareRequestDto.builder()
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destinationLatitude(12.8797)
                .destinationLongitude(77.6850)
                .build();

        FareResponseDto response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertTrue(response.getDistanceKm().doubleValue() > 13 && response.getDistanceKm().doubleValue() < 15,
                "Distance was: " + response.getDistanceKm());
        assertEquals(BigDecimal.ZERO, response.getTimeSurcharge());
        assertTrue(response.getBaseFare().doubleValue() > 180 && response.getBaseFare().doubleValue() < 210);
        assertEquals(response.getBaseFare(), response.getTotalFare());
    }

    @Test
    public void testCalculateFare_WithTimeNoSurcharge() {
        ReflectionTestUtils.setField(fareCalculationService, "farePerKm", 14.0);
        ReflectionTestUtils.setField(fareCalculationService, "expectedSpeedKmph", 20.0);

        FareRequestDto request = FareRequestDto.builder()
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destinationLatitude(12.8797)
                .destinationLongitude(77.6850)
                .actualTimeMinutes(25) // Less than expected time
                .build();

        FareResponseDto response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getTimeSurcharge());
        assertEquals(response.getBaseFare(), response.getTotalFare());
    }

    @Test
    public void testCalculateFare_WithTimeSurcharge() {
        ReflectionTestUtils.setField(fareCalculationService, "farePerKm", 14.0);
        ReflectionTestUtils.setField(fareCalculationService, "expectedSpeedKmph", 20.0);

        FareRequestDto request = FareRequestDto.builder()
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destinationLatitude(12.8797)
                .destinationLongitude(77.6850)
                .actualTimeMinutes(45) // More than expected time
                .build();

        FareResponseDto response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertTrue(response.getTimeSurcharge().doubleValue() > 0);
        assertTrue(response.getTotalFare().doubleValue() > response.getBaseFare().doubleValue());
    }

    @Test
    public void testCalculateFare_ZeroDistance() {
        ReflectionTestUtils.setField(fareCalculationService, "farePerKm", 14.0);
        ReflectionTestUtils.setField(fareCalculationService, "expectedSpeedKmph", 20.0);

        FareRequestDto request = FareRequestDto.builder()
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destinationLatitude(12.9716)
                .destinationLongitude(77.5946)
                .build();

        FareResponseDto response = fareCalculationService.calculateFare(request);

        assertNotNull(response);
        assertEquals(0.0, response.getDistanceKm().doubleValue());
        assertEquals(0.0, response.getBaseFare().doubleValue());
        assertEquals(0.0, response.getTotalFare().doubleValue());
    }
}
