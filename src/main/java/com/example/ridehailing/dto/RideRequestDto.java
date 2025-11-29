package com.example.ridehailing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {
    private Long userId;
    private double startLatitude;
    private double startLongitude;
    private double destLatitude;
    private double destLongitude;
}
