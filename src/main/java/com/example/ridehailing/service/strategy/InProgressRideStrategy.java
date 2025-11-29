package com.example.ridehailing.service.strategy;

import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.ridehailing.model.RideUpdateType.*;

@Component
public class InProgressRideStrategy implements RideUpdateStrategy {

    private final RideUpdatePublisher rideUpdatePublisher;

    public InProgressRideStrategy(RideUpdatePublisher rideUpdatePublisher) {
        this.rideUpdatePublisher = rideUpdatePublisher;
    }

    @Override
    public boolean isApplicable(com.example.ridehailing.model.RideUpdateType updateType) {
        return updateType == IN_PROGRESS;
    }

    @Override
    public void updateRide(Ride ride, Long driverId) {
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new ValidationException("Ride must be in ASSIGNED state to start");
        }

        if (!ride.getDriverId().equals(driverId)) {
            throw new ValidationException("Only the assigned driver can start the ride");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setRideStartTime(LocalDateTime.now());

        rideUpdatePublisher.publishRideUpdate(ride.getId(), ride.getUserId(), RideStatus.IN_PROGRESS);
    }
}
