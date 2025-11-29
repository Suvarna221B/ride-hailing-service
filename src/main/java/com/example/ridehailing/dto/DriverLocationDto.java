package com.example.ridehailing.dto;

import com.example.ridehailing.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDto implements Serializable {
    private Double latitude;
    private Double longitude;
    private DriverStatus status;
}
