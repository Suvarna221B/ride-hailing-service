package com.example.ridehailing.dto;

import com.example.ridehailing.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateMessage {
    private Long driverId;
    private DriverStatus status;
}
