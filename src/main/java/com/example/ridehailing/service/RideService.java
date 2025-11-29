package com.example.ridehailing.service;

import com.example.ridehailing.dto.*;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.PaymentRequestPublisher;
import com.example.ridehailing.kafka.publisher.RideRequestPublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.RideUpdateType;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.RideRepository;
import com.example.ridehailing.service.strategy.RideUpdateStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RideService {

        private final RideRepository rideRepository;
        private final DriverService driverService;
        private final FareCalculationService fareCalculationService;
        private final UserService userService;
        private final RideRequestPublisher rideRequestPublisher;
        private final RideUpdateStrategyFactory rideUpdateStrategyFactory;
        private final PaymentRequestPublisher paymentRequestPublisher;

        public RideService(RideRepository rideRepository,
                        DriverService driverService,
                        FareCalculationService fareCalculationService,
                        UserService userService,
                        RideRequestPublisher rideRequestPublisher,
                        RideUpdateStrategyFactory rideUpdateStrategyFactory,
                        PaymentRequestPublisher paymentRequestPublisher) {
                this.rideRepository = rideRepository;
                this.driverService = driverService;
                this.fareCalculationService = fareCalculationService;
                this.userService = userService;
                this.rideRequestPublisher = rideRequestPublisher;
                this.rideUpdateStrategyFactory = rideUpdateStrategyFactory;
                this.paymentRequestPublisher = paymentRequestPublisher;
        }

        @Transactional
        public RideResponseDto createRide(RideRequestDto request) {
                log.info("Creating ride for user ID: {}", request.getUserId());
                User user = userService.getUserEntityById(request.getUserId());
                FareResponseDto fareResponse = calculateFare(request);

                Ride ride = createRideEntity(request, user, fareResponse);
                log.info("Ride created with ID: {}", ride.getId());

                List<Long> nearbyDriverIds = driverService.findNearbyDrivers(
                                request.getStartLatitude(),
                                request.getStartLongitude(),
                                5.0);

                if (!nearbyDriverIds.isEmpty()) {
                        log.info("Found {} nearby drivers for ride ID: {}", nearbyDriverIds.size(), ride.getId());
                        rideRequestPublisher.publishRideRequest(
                                        UUID.randomUUID().toString(),
                                        ride.getId(),
                                        nearbyDriverIds);
                } else {
                        log.warn("No nearby drivers found for ride ID: {}", ride.getId());
                }

                return RideResponseDto.builder()
                                .rideId(ride.getId())
                                .status(ride.getStatus())
                                .fare(ride.getFare())
                                .build();
        }

        @Transactional
        public void updateRide(Long rideId, Long driverId, RideUpdateType updateType) {
                log.info("Updating ride ID: {} with type: {}", rideId, updateType);
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> {
                                        log.error("Ride not found with ID: {}", rideId);
                                        return new ValidationException("Ride not found");
                                });

                rideUpdateStrategyFactory.getStrategy(updateType).updateRide(ride, driverId);
                rideRepository.save(ride);
                log.info("Ride ID: {} updated successfully", rideId);
        }

        @Transactional
        public void completeRideWithPayment(Long rideId, Long paymentId) {
                log.info("Completing ride ID: {} with payment ID: {}", rideId, paymentId);
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> {
                                        log.error("Ride not found with ID: {}", rideId);
                                        return new ValidationException("Ride not found");
                                });

                ride.setPaymentId(paymentId.toString());

                updateRide(rideId, ride.getDriverId(), RideUpdateType.COMPLETED);
        }

        @Transactional
        public void processPayment(Long rideId, BigDecimal paymentAmount,
                        com.example.ridehailing.model.PaymentMethod paymentMethod) {
                log.info("Processing payment for ride ID: {}, amount: {}", rideId, paymentAmount);
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> {
                                        log.error("Ride not found with ID: {}", rideId);
                                        return new ValidationException("Ride not found");
                                });

                if (ride.getStatus() != RideStatus.PAYMENT_PENDING) {
                        log.error("Ride ID: {} is not in PAYMENT_PENDING state", rideId);
                        throw new ValidationException("Ride must be in PAYMENT_PENDING state for payment");
                }

                if (ride.getFare().compareTo(paymentAmount) != 0) {
                        log.error("Payment amount mismatch for ride ID: {}. Expected: {}, Received: {}", rideId,
                                        ride.getFare(), paymentAmount);
                        throw new ValidationException("Payment amount does not match ride fare. Expected: "
                                        + ride.getFare() + ", Received: " + paymentAmount);
                }

                paymentRequestPublisher.publishPaymentRequest(ride.getId(), ride.getUserId(), paymentAmount,
                                paymentMethod);
                log.info("Payment request published for ride ID: {}", rideId);
        }

        public RideResponseDto getRideById(Long rideId) {
                log.info("Fetching ride with ID: {}", rideId);
                Ride ride = rideRepository.findById(rideId)
                                .orElseThrow(() -> {
                                        log.error("Ride not found with ID: {}", rideId);
                                        return new ValidationException("Ride not found");
                                });

                return RideResponseDto.builder()
                                .rideId(ride.getId())
                                .status(ride.getStatus())
                                .fare(ride.getFare())
                                .pickupLocation(new LocationDto(ride.getStartLatitude(),
                                                ride.getStartLongitude()))
                                .dropoffLocation(new LocationDto(ride.getDestLatitude(),
                                                ride.getDestLongitude()))
                                .driverId(ride.getDriverId())
                                .riderId(ride.getUserId())
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
