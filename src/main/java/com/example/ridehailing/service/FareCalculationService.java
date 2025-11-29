package com.example.ridehailing.service;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareCalculationService {

    @Value("${fare.per.km}")
    private double farePerKm;

    @Value("${fare.expected.speed.kmph}")
    private double expectedSpeedKmph;

    public FareResponseDto calculateFare(FareRequestDto request) {
        double distanceKm = calculateDistance(
                request.getStartLatitude(),
                request.getStartLongitude(),
                request.getDestinationLatitude(),
                request.getDestinationLongitude());

        BigDecimal distanceKmBd = BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP);
        BigDecimal farePerKmBd = BigDecimal.valueOf(farePerKm);

        // Calculate base fare
        BigDecimal baseFare = distanceKmBd.multiply(farePerKmBd).setScale(2, RoundingMode.HALF_UP);

        // if the ride time took more time (more traffic) calculated updated fee
        BigDecimal timeSurcharge = BigDecimal.ZERO;
        if (request.getActualTimeMinutes() != null) {
            double expectedTimeMinutes = (distanceKm / expectedSpeedKmph) * 60;
            if (request.getActualTimeMinutes() > expectedTimeMinutes) {
                double delayMinutes = request.getActualTimeMinutes() - expectedTimeMinutes;
                // Surcharge: (delay / 15) * (baseFare * 0.5)
                BigDecimal delayFactor = BigDecimal.valueOf(delayMinutes / 15.0);
                timeSurcharge = delayFactor.multiply(baseFare).multiply(BigDecimal.valueOf(0.5))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        BigDecimal totalFare = baseFare.add(timeSurcharge).setScale(2, RoundingMode.HALF_UP);

        return FareResponseDto.builder()
                .distanceKm(distanceKmBd)
                .baseFare(baseFare)
                .timeSurcharge(timeSurcharge)
                .totalFare(totalFare)
                .build();
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
