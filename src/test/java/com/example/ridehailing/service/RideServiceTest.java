package com.example.ridehailing.service;

import com.example.ridehailing.dto.*;
import com.example.ridehailing.kafka.publisher.RideRequestPublisher;
import com.example.ridehailing.kafka.publisher.RideUpdatePublisher;
import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.RideRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

        @InjectMocks
        private RideService rideService;

        @Mock
        private RideRepository rideRepository;

        @Mock
        private DriverService driverService;

        @Mock
        private FareCalculationService fareCalculationService;

        @Mock
        private UserService userService;

        @Mock
        private RideRequestPublisher rideRequestPublisher;

        @Mock
        private RideUpdatePublisher rideUpdatePublisher;

        @Test
        public void testCreateRide_Success() {
                Long userId = 1L;
                RideRequestDto request = RideRequestDto.builder()
                                .userId(userId)
                                .startLatitude(12.9716)
                                .startLongitude(77.5946)
                                .destLatitude(12.8797)
                                .destLongitude(77.6850)
                                .build();

                User user = new User();
                user.setId(userId);
                when(userService.getUserEntityById(userId)).thenReturn(user);

                FareResponseDto fareResponse = FareResponseDto.builder()
                                .totalFare(BigDecimal.valueOf(150.0))
                                .build();
                when(fareCalculationService.calculateFare(any(FareRequestDto.class))).thenReturn(fareResponse);

                Ride ride = Ride.builder()
                                .id(100L)
                                .userId(userId)
                                .status(RideStatus.REQUESTED)
                                .fare(BigDecimal.valueOf(150.0))
                                .build();
                when(rideRepository.save(any(Ride.class))).thenReturn(ride);

                List<Long> driverIds = Arrays.asList(10L, 20L);
                when(driverService.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble())).thenReturn(driverIds);

                RideResponseDto response = rideService.createRide(request);

                assertNotNull(response);
                assertEquals(100L, response.getRideId());
                assertEquals(RideStatus.REQUESTED, response.getStatus());
                assertEquals(BigDecimal.valueOf(150.0), response.getFare());

                verify(rideRequestPublisher, times(1)).publishRideRequest(anyString(), eq(100L), eq(driverIds));
        }

        @Test
        public void testAcceptRide_Success() {
                Long rideId = 1L;
                Long driverId = 20L;

                Ride ride = Ride.builder()
                                .id(rideId)
                                .userId(10L)
                                .status(RideStatus.REQUESTED)
                                .build();

                when(rideRepository.findById(rideId)).thenReturn(java.util.Optional.of(ride));

                rideService.acceptRide(rideId, driverId);

                assertEquals(RideStatus.IN_PROGRESS, ride.getStatus());
                assertEquals(driverId, ride.getDriverId());
                verify(rideRepository).save(ride);
                verify(driverService).updateDriverStatus(driverId, "BUSY");
                verify(rideUpdatePublisher).publishRideUpdate(ride.getId(), ride.getUserId(), RideStatus.IN_PROGRESS);
        }
}
