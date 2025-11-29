package com.example.ridehailing.service.strategy;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.service.DriverService;
import com.example.ridehailing.service.FareCalculationService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.ridehailing.model.RideUpdateType.*;

@Component
public class PaymentPendingRideStrategy implements RideUpdateStrategy {

    private final RideUpdatePublisher rideUpdatePublisher;
    private final FareCalculationService fareCalculationService;
    private final DriverService driverService;

    public PaymentPendingRideStrategy(RideUpdatePublisher rideUpdatePublisher,
            FareCalculationService fareCalculationService,
            DriverService driverService) {
        this.rideUpdatePublisher = rideUpdatePublisher;
        this.fareCalculationService = fareCalculationService;
        this.driverService = driverService;
    }

    @Override
    public boolean isApplicable(com.example.ridehailing.model.RideUpdateType updateType) {
        return updateType == PAYMENT_PENDING;
    }

    @Override
    public void updateRide(Ride ride, Long driverId) {
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new ValidationException("Ride must be in IN_PROGRESS state to mark as payment pending");
        }

        if (!ride.getDriverId().equals(driverId)) {
            throw new ValidationException("Only the assigned driver can complete the ride");
        }

        if (ride.getRideStartTime() == null) {
            throw new ValidationException("Ride start time is not set");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Duration rideDuration = Duration.between(ride.getRideStartTime(), currentTime);
        long actualTimeMinutes = rideDuration.toMinutes();

        FareRequestDto fareRequest = FareRequestDto.builder()
                .startLatitude(ride.getStartLatitude())
                .startLongitude(ride.getStartLongitude())
                .destinationLatitude(ride.getDestLatitude())
                .destinationLongitude(ride.getDestLongitude())
                .actualTimeMinutes((int) actualTimeMinutes)
                .build();

        FareResponseDto fareResponse = fareCalculationService.calculateFare(fareRequest);

        // Update fare if it has changed
        BigDecimal newFare = fareResponse.getTotalFare();
        if (newFare.compareTo(ride.getFare()) != 0) {
            ride.setFare(newFare);
        }

        ride.setStatus(RideStatus.PAYMENT_PENDING);

        driverService.updateDriverStatusUsingDriverId(driverId, "available");

        // Publish update with fare information
        rideUpdatePublisher.publishRideUpdate(
                ride.getId(),
                ride.getUserId(),
                RideStatus.PAYMENT_PENDING,
                ride.getFare());
    }
}
