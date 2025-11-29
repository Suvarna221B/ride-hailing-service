package com.example.ridehailing.service.strategy;

import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompletedRideStrategyTest {

    private final com.example.ridehailing.kafka.publisher.RideUpdatePublisher rideUpdatePublisher = mock(
            com.example.ridehailing.kafka.publisher.RideUpdatePublisher.class);
    private final CompletedRideStrategy strategy = new CompletedRideStrategy(rideUpdatePublisher);

    @Test
    public void testIsApplicable() {
        assertTrue(strategy.isApplicable(RideUpdateType.COMPLETED));
        assertFalse(strategy.isApplicable(RideUpdateType.ACCEPT));
    }

    @Test
    public void testUpdateRide_Success() {
        Ride ride = Ride.builder()
                .id(1L)
                .userId(10L)
                .status(RideStatus.PAYMENT_PENDING)
                .build();

        strategy.updateRide(ride, 1L);

        assertEquals(RideStatus.COMPLETED, ride.getStatus());
        verify(rideUpdatePublisher).publishRideUpdate(1L, 10L, RideStatus.COMPLETED);
    }

    @Test
    public void testUpdateRide_InvalidStatus() {
        Ride ride = Ride.builder()
                .status(RideStatus.IN_PROGRESS)
                .build();

        assertThrows(IllegalStateException.class, () -> {
            strategy.updateRide(ride, 1L);
        });
    }
}
