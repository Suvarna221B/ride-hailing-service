package com.example.ridehailing.service.strategy;

import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.service.DriverService;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import static com.example.ridehailing.model.RideUpdateType.*;

@Component
@Slf4j
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
        log.info("Driver {} accepting ride {}", driverId, ride.getId());
        if (ride.getStatus() != RideStatus.REQUESTED) {
            log.warn("Ride {} is not in REQUESTED state. Current status: {}", ride.getId(), ride.getStatus());
            throw new ValidationException("Ride is not in REQUESTED state");
        }

        if (ride.getDriverId() != null) {
            log.warn("Ride {} is already assigned to driver {}", ride.getId(), ride.getDriverId());
            throw new ValidationException("Ride is already assigned");
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);

        driverService.updateDriverStatusUsingDriverId(driverId, "busy");

        rideUpdatePublisher.publishRideUpdate(ride.getId(), ride.getUserId(), RideStatus.ASSIGNED);
        log.info("Ride {} assigned to driver {}", ride.getId(), driverId);
    }
}
