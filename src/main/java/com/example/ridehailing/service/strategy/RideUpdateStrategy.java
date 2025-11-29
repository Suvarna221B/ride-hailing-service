package com.example.ridehailing.service.strategy;

import com.example.ridehailing.model.Ride;

public interface RideUpdateStrategy {
    boolean isApplicable(com.example.ridehailing.model.RideUpdateType updateType);

    void updateRide(Ride ride, Long driverId);
}
