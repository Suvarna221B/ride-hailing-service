package com.example.ridehailing.service.strategy;

import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CompletedRideStrategy implements RideUpdateStrategy {

    private final com.example.ridehailing.kafka.publisher.RideUpdatePublisher rideUpdatePublisher;

    public CompletedRideStrategy(com.example.ridehailing.kafka.publisher.RideUpdatePublisher rideUpdatePublisher) {
        this.rideUpdatePublisher = rideUpdatePublisher;
    }

    @Override
    public boolean isApplicable(RideUpdateType updateType) {
        return RideUpdateType.COMPLETED == updateType;
    }

    @Override
    public void updateRide(Ride ride, Long driverId) {
        log.info("Completing ride {}", ride.getId());
        if (ride.getStatus() != RideStatus.PAYMENT_PENDING) {
            log.error("Ride {} cannot be completed. Current status: {}", ride.getId(), ride.getStatus());
            throw new IllegalStateException("Ride cannot be completed unless payment is pending");
        }
        ride.setStatus(RideStatus.COMPLETED);

        // Publish ride update notification to user
        rideUpdatePublisher.publishRideUpdate(ride.getId(), ride.getUserId(), RideStatus.COMPLETED);
        log.info("Ride {} completed successfully", ride.getId());
    }
}
