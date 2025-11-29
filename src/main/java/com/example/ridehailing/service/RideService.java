package com.example.ridehailing.service;

import com.example.ridehailing.dto.*;
import com.example.ridehailing.kafka.publisher.RideRequestPublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RideService {

        private final RideRepository rideRepository;
        private final DriverService driverService;
        private final FareCalculationService fareCalculationService;
        private final UserService userService;
        private final RideRequestPublisher rideRequestPublisher;

        public RideService(RideRepository rideRepository,
                        DriverService driverService,
                        FareCalculationService fareCalculationService,
                        UserService userService,
                        RideRequestPublisher rideRequestPublisher) {
                this.rideRepository = rideRepository;
                this.driverService = driverService;
                this.fareCalculationService = fareCalculationService;
                this.userService = userService;
                this.rideRequestPublisher = rideRequestPublisher;
        }

        @Transactional
        public RideResponseDto createRide(RideRequestDto request) {
            User user = userService.getUserEntityById(request.getUserId());
            FareResponseDto fareResponse = calculateFare(request);

            Ride ride = createRideEntity(request, user, fareResponse);

            List<Long> nearbyDriverIds = driverService.findNearbyDrivers(
                                request.getStartLatitude(),
                                request.getStartLongitude(),
                                5.0);

            if (!nearbyDriverIds.isEmpty()) {
                    rideRequestPublisher.publishRideRequest(
                                    UUID.randomUUID().toString(),
                                    ride.getId(),
                                    nearbyDriverIds);
            }

            return RideResponseDto.builder()
                            .rideId(ride.getId())
                            .status(ride.getStatus())
                            .fare(ride.getFare())
                            .build();
        }

    private Ride createRideEntity(RideRequestDto request, User user, FareResponseDto fareResponse) {
        Ride ride = Ride.builder()
                        .userId(user.getId())
                        .startLatitude(request.getStartLatitude())
                        .startLongitude(request.getStartLongitude())
                        .destLatitude(request.getDestLatitude())
                        .destLongitude(request.getDestLongitude())
                        .fare(fareResponse.getTotalFare())
                        .status(RideStatus.REQUESTED)
                        .build();
        ride = rideRepository.save(ride);
        return ride;
    }

    private FareResponseDto calculateFare(RideRequestDto request) {
                FareRequestDto fareRequest = FareRequestDto.builder()
                                .startLatitude(request.getStartLatitude())
                                .startLongitude(request.getStartLongitude())
                                .destinationLatitude(request.getDestLatitude())
                                .destinationLongitude(request.getDestLongitude())
                                .build();
                return fareCalculationService.calculateFare(fareRequest);
        }
}
