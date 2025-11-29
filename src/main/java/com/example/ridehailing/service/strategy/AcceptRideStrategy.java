package com.example.ridehailing.service.strategy;

import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.service.DriverService;
import org.springframework.stereotype.Component;

import static com.example.ridehailing.model.RideUpdateType.*;

@Component
public class AcceptRideStrategy implements RideUpdateStrategy {

    private final DriverService driverService;
    private final RideUpdatePublisher rideUpdatePublisher;

    public AcceptRideStrategy(DriverService driverService, RideUpdatePublisher rideUpdatePublisher) {
        this.driverService = driverService;
        this.rideUpdatePublisher = rideUpdatePublisher;
    }

    @Override
    public boolean isApplicable(com.example.ridehailing.model.RideUpdateType updateType) {
        return updateType == ACCEPT;
    }

    @Override
    public void updateRide(Ride ride, Long driverId) {
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new ValidationException("Ride is not in REQUESTED state");
        }

        if (ride.getDriverId() != null) {
            throw new ValidationException("Ride is already assigned");
        }

        ride.setStatus(RideStatus.ASSIGNED);
        ride.setDriverId(driverId);

        driverService.updateDriverStatus(driverId, "BUSY");

        rideUpdatePublisher.publishRideUpdate(ride.getId(), ride.getUserId(), RideStatus.ASSIGNED);
    }
}
