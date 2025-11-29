package com.example.ridehailing.dto;

import com.example.ridehailing.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {
    private Long rideId;
    private RideStatus status;
    private BigDecimal fare;
    private LocationDto pickupLocation;
    private LocationDto dropoffLocation;
    private Long driverId;
    private Long riderId;
}
