package com.example.ridehailing.service.strategy;

import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InProgressRideStrategyTest {

    @InjectMocks
    private InProgressRideStrategy inProgressRideStrategy;

    @Mock
    private RideUpdatePublisher rideUpdatePublisher;

    @Test
    public void testIsApplicable_InProgress() {
        assertTrue(inProgressRideStrategy.isApplicable(RideUpdateType.IN_PROGRESS));
    }

    @Test
    public void testIsApplicable_OtherTypes() {
        assertFalse(inProgressRideStrategy.isApplicable(RideUpdateType.ACCEPT));
        assertFalse(inProgressRideStrategy.isApplicable(RideUpdateType.PAYMENT_PENDING));
        assertFalse(inProgressRideStrategy.isApplicable(RideUpdateType.COMPLETED));
    }

    @Test
    public void testUpdateRide_Success() {
        Long rideId = 1L;
        Long userId = 10L;
        Long driverId = 20L;

        Ride ride = Ride.builder()
                .id(rideId)
                .userId(userId)
                .driverId(driverId)
                .status(RideStatus.ASSIGNED)
                .build();

        inProgressRideStrategy.updateRide(ride, driverId);

        assertEquals(RideStatus.IN_PROGRESS, ride.getStatus());
        assertNotNull(ride.getRideStartTime());
        verify(rideUpdatePublisher).publishRideUpdate(rideId, userId, RideStatus.IN_PROGRESS);
    }

    @Test
    public void testUpdateRide_NotAssignedStatus() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .driverId(20L)
                .status(RideStatus.REQUESTED)
                .build();

        assertThrows(ValidationException.class, () -> {
            inProgressRideStrategy.updateRide(ride, 20L);
        });

        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any());
    }

    @Test
    public void testUpdateRide_WrongDriver() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .driverId(20L)
                .status(RideStatus.ASSIGNED)
                .build();

        assertThrows(ValidationException.class, () -> {
            inProgressRideStrategy.updateRide(ride, 99L);
        });

        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any());
    }
}
