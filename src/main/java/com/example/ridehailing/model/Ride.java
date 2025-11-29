package com.example.ridehailing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides", indexes = {
        @Index(name = "idx_ride_user_id", columnList = "userId"),
        @Index(name = "idx_ride_driver_id", columnList = "driverId"),
        @Index(name = "idx_ride_status", columnList = "status"),
        @Index(name = "idx_ride_user_status", columnList = "userId, status"),
        @Index(name = "idx_ride_driver_status", columnList = "driverId, status"),
        @Index(name = "idx_ride_start_time", columnList = "rideStartTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long driverId;

    @Column(nullable = false)
    private double startLatitude;

    @Column(nullable = false)
    private double startLongitude;

    @Column(nullable = false)
    private double destLatitude;

    @Column(nullable = false)
    private double destLongitude;

    @Column(nullable = false)
    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    private String paymentId;

    private LocalDateTime rideStartTime;
}
