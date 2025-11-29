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
public class RideUpdateMessage {
    private Long rideId;
    private Long userId;
    private RideStatus status;
    private BigDecimal fare;
}
