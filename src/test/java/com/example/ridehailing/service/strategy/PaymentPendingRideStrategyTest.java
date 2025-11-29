package com.example.ridehailing.service.strategy;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import com.example.ridehailing.service.FareCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentPendingRideStrategyTest {

    @InjectMocks
    private PaymentPendingRideStrategy paymentPendingRideStrategy;

    @Mock
    private RideUpdatePublisher rideUpdatePublisher;

    @Mock
    private FareCalculationService fareCalculationService;

    @Test
    public void testIsApplicable_PaymentPending() {
        assertTrue(paymentPendingRideStrategy.isApplicable(RideUpdateType.PAYMENT_PENDING));
    }

    @Test
    public void testIsApplicable_OtherTypes() {
        assertFalse(paymentPendingRideStrategy.isApplicable(RideUpdateType.ACCEPT));
        assertFalse(paymentPendingRideStrategy.isApplicable(RideUpdateType.IN_PROGRESS));
        assertFalse(paymentPendingRideStrategy.isApplicable(RideUpdateType.COMPLETED));
    }

    @Test
    public void testUpdateRide_Success_FareUpdated() {
        Long rideId = 1L;
        Long userId = 10L;
        Long driverId = 20L;
        BigDecimal originalFare = BigDecimal.valueOf(100.0);
        BigDecimal newFare = BigDecimal.valueOf(150.0);

        Ride ride = Ride.builder()
                .id(rideId)
                .userId(userId)
                .driverId(driverId)
                .status(RideStatus.IN_PROGRESS)
                .fare(originalFare)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .rideStartTime(LocalDateTime.now().minusMinutes(30))
                .build();

        FareResponseDto fareResponse = FareResponseDto.builder()
                .totalFare(newFare)
                .build();

        when(fareCalculationService.calculateFare(any(FareRequestDto.class))).thenReturn(fareResponse);

        paymentPendingRideStrategy.updateRide(ride, driverId);

        assertEquals(RideStatus.PAYMENT_PENDING, ride.getStatus());
        assertEquals(newFare, ride.getFare());
        verify(rideUpdatePublisher).publishRideUpdate(rideId, userId, RideStatus.PAYMENT_PENDING, newFare);
    }

    @Test
    public void testUpdateRide_Success_FareUnchanged() {
        Long rideId = 1L;
        Long userId = 10L;
        Long driverId = 20L;
        BigDecimal fare = BigDecimal.valueOf(150.0);

        Ride ride = Ride.builder()
                .id(rideId)
                .userId(userId)
                .driverId(driverId)
                .status(RideStatus.IN_PROGRESS)
                .fare(fare)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .rideStartTime(LocalDateTime.now().minusMinutes(20))
                .build();

        FareResponseDto fareResponse = FareResponseDto.builder()
                .totalFare(fare)
                .build();

        when(fareCalculationService.calculateFare(any(FareRequestDto.class))).thenReturn(fareResponse);

        paymentPendingRideStrategy.updateRide(ride, driverId);

        assertEquals(RideStatus.PAYMENT_PENDING, ride.getStatus());
        assertEquals(fare, ride.getFare());
        verify(rideUpdatePublisher).publishRideUpdate(rideId, userId, RideStatus.PAYMENT_PENDING, fare);
    }

    @Test
    public void testUpdateRide_NotInProgressStatus() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .driverId(20L)
                .status(RideStatus.ASSIGNED)
                .build();

        assertThrows(ValidationException.class, () -> {
            paymentPendingRideStrategy.updateRide(ride, 20L);
        });

        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any(), any());
    }

    @Test
    public void testUpdateRide_WrongDriver() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .driverId(20L)
                .status(RideStatus.IN_PROGRESS)
                .rideStartTime(LocalDateTime.now())
                .build();

        assertThrows(ValidationException.class, () -> {
            paymentPendingRideStrategy.updateRide(ride, 99L);
        });

        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any(), any());
    }

    @Test
    public void testUpdateRide_NoStartTime() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .driverId(20L)
                .status(RideStatus.IN_PROGRESS)
                .build();

        assertThrows(ValidationException.class, () -> {
            paymentPendingRideStrategy.updateRide(ride, 20L);
        });

        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any(), any());
    }
}
