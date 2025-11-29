package com.example.ridehailing.service.strategy;

import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import com.example.ridehailing.service.DriverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AcceptRideStrategyTest {

    @InjectMocks
    private AcceptRideStrategy acceptRideStrategy;

    @Mock
    private DriverService driverService;

    @Mock
    private RideUpdatePublisher rideUpdatePublisher;

    @Test
    public void testIsApplicable_Accept() {
        assertTrue(acceptRideStrategy.isApplicable(RideUpdateType.ACCEPT));
    }

    @Test
    public void testIsApplicable_OtherTypes() {
        assertFalse(acceptRideStrategy.isApplicable(RideUpdateType.IN_PROGRESS));
        assertFalse(acceptRideStrategy.isApplicable(RideUpdateType.PAYMENT_PENDING));
        assertFalse(acceptRideStrategy.isApplicable(RideUpdateType.COMPLETED));
    }

    @Test
    public void testUpdateRide_Success() {
        Long rideId = 1L;
        Long userId = 10L;
        Long driverId = 20L;

        Ride ride = Ride.builder()
                .id(rideId)
                .userId(userId)
                .status(RideStatus.REQUESTED)
                .build();

        acceptRideStrategy.updateRide(ride, driverId);

        assertEquals(RideStatus.ASSIGNED, ride.getStatus());
        assertEquals(driverId, ride.getDriverId());
        verify(driverService).updateDriverStatus(driverId, "busy");
        verify(rideUpdatePublisher).publishRideUpdate(rideId, userId, RideStatus.ASSIGNED);
    }

    @Test
    public void testUpdateRide_NotRequestedStatus() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .status(RideStatus.IN_PROGRESS)
                .build();

        assertThrows(ValidationException.class, () -> {
            acceptRideStrategy.updateRide(ride, 20L);
        });

        verify(driverService, never()).updateDriverStatus(any(), any());
        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any());
    }

    @Test
    public void testUpdateRide_AlreadyAssigned() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .status(RideStatus.REQUESTED)
                .driverId(15L)
                .build();

        assertThrows(ValidationException.class, () -> {
            acceptRideStrategy.updateRide(ride, 20L);
        });

        verify(driverService, never()).updateDriverStatus(any(), any());
        verify(rideUpdatePublisher, never()).publishRideUpdate(any(), any(), any());
    }
}
