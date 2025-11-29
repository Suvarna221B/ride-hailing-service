package com.example.ridehailing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_ride_id", columnList = "rideId"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_time", columnList = "paymentTime"),
        @Index(name = "idx_payment_transaction_id", columnList = "transactionId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED

    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime paymentTime;
}
