package com.example.ridehailing.repository;

import com.example.ridehailing.model.Ride;
import com.example.ridehailing.model.RideStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RideRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    @Test
    public void testSaveRide() {
        Ride ride = Ride.builder()
                .userId(100L)
                .driverId(200L)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .fare(BigDecimal.valueOf(150.0))
                .status(RideStatus.REQUESTED)
                .build();

        Ride saved = rideRepository.save(ride);

        assertNotNull(saved.getId());
        assertEquals(100L, saved.getUserId());
        assertEquals(200L, saved.getDriverId());
        assertEquals(RideStatus.REQUESTED, saved.getStatus());
        assertEquals(BigDecimal.valueOf(150.0), saved.getFare());
    }

    @Test
    public void testFindById_Found() {
        Ride ride = Ride.builder()
                .userId(100L)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .fare(BigDecimal.valueOf(100.0))
                .status(RideStatus.IN_PROGRESS)
                .rideStartTime(LocalDateTime.now())
                .build();
        entityManager.persist(ride);
        entityManager.flush();

        Optional<Ride> found = rideRepository.findById(ride.getId());

        assertTrue(found.isPresent());
        assertEquals(100L, found.get().getUserId());
        assertEquals(RideStatus.IN_PROGRESS, found.get().getStatus());
        assertNotNull(found.get().getRideStartTime());
    }

    @Test
    public void testFindById_NotFound() {
        Optional<Ride> found = rideRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    public void testUpdateRide() {
        Ride ride = Ride.builder()
                .userId(100L)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .fare(BigDecimal.valueOf(100.0))
                .status(RideStatus.REQUESTED)
                .build();
        entityManager.persist(ride);
        entityManager.flush();

        ride.setStatus(RideStatus.ASSIGNED);
        ride.setDriverId(200L);
        Ride updated = rideRepository.save(ride);

        assertEquals(RideStatus.ASSIGNED, updated.getStatus());
        assertEquals(200L, updated.getDriverId());
    }
}
