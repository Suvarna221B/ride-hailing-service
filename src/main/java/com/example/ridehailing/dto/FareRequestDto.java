package com.example.ridehailing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareRequestDto {
    private double startLatitude;
    private double startLongitude;
    private double destinationLatitude;
    private double destinationLongitude;
    private Integer actualTimeMinutes; // Optional
}
