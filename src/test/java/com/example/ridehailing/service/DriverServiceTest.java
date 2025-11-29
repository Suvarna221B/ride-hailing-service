package com.example.ridehailing.service;

import com.example.ridehailing.dto.DriverLocationDto;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.model.DriverStatus;
import com.example.ridehailing.model.Driver;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private DriverService driverService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserService userService;

    @Mock
    private GeoOperations<String, Object> geoOperations;

    @Mock
    private com.example.ridehailing.kafka.publisher.DriverUpdatePublisher driverUpdatePublisher;

    @Test
    public void testUpdateLocation_Success() {
        Long userId = 1L;
        DriverLocationDto locationDto = DriverLocationDto.builder()
                .latitude(12.9716)
                .longitude(77.5946)
                .build();

        Driver driver = Driver.builder()
                .id(10L)
                .user(new User())
                .status(DriverStatus.AVAILABLE)
                .build();

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);

        driverService.updateLocation(userId, locationDto);

        verify(geoOperations).add(eq("drivers:geo"), any(Point.class), eq(10L));
    }

    @Test
    public void testUpdateLocation_DriverNotAvailable() {
        Long driverId = 1L;
        DriverLocationDto locationDto = new DriverLocationDto();
        locationDto.setLatitude(12.9716);
        locationDto.setLongitude(77.5946);

        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setStatus(DriverStatus.OFFLINE); // Driver is OFFLINE

        when(driverRepository.findByUserId(driverId)).thenReturn(Optional.of(driver));
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);

        driverService.updateLocation(driverId, locationDto);

        // Verify geo operations are called even for OFFLINE drivers
        verify(geoOperations).add(eq("drivers:geo"), any(Point.class), eq(driverId));
    }

    @Test
    public void testFindNearbyDrivers() {
        double lat = 12.9716;
        double lon = 77.5946;
        double radius = 5.0;

        RedisGeoCommands.GeoLocation<Object> geoLocation = new RedisGeoCommands.GeoLocation<>(1L, new Point(lon, lat));
        GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult = new GeoResult<>(geoLocation, new Distance(1.0));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = new GeoResults<>(
                Collections.singletonList(geoResult));

        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        when(geoOperations.radius(eq("drivers:geo"), any(Circle.class))).thenReturn(geoResults);

        List<Long> driverIds = driverService.findNearbyDrivers(lat, lon, radius);

        assertNotNull(driverIds);
        assertEquals(1, driverIds.size());
        assertEquals(1L, driverIds.get(0));
    }

    @Test
    public void testRegisterDriver_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("driver1");

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userService.getUserEntityById(userId)).thenReturn(user);

        driverService.registerDriver(userId);

        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    public void testRegisterDriver_AlreadyRegistered() {
        Long userId = 1L;
        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(new Driver()));

        assertThrows(ValidationException.class, () -> driverService.registerDriver(userId));
    }

    @Test
    public void testUpdateDriverStatus_Success() {
        Long userId = 1L;
        Driver driver = Driver.builder()
                .id(1L)
                .name("driver1")
                .status(DriverStatus.OFFLINE)
                .build();

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));

        driverService.updateDriverStatusUsingUserId(userId, "available");

        verify(driverRepository).save(any(Driver.class));
        verify(driverUpdatePublisher).publishDriverUpdate(any());
    }

    @Test
    public void testUpdateDriverStatus_ToBusy_RemovesFromRedis() {
        Long userId = 1L;
        Driver driver = Driver.builder()
                .id(10L)
                .name("driver1")
                .status(DriverStatus.AVAILABLE)
                .build();

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);

        driverService.updateDriverStatusUsingUserId(userId, "busy");

        verify(geoOperations).remove("drivers", "10");
        verify(driverRepository).save(any(Driver.class));
        verify(driverUpdatePublisher).publishDriverUpdate(any());
    }

    @Test
    public void testUpdateDriverStatus_ToOffline_RemovesFromRedis() {
        Long userId = 1L;
        Driver driver = Driver.builder()
                .id(10L)
                .name("driver1")
                .status(DriverStatus.AVAILABLE)
                .build();

        when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);

        driverService.updateDriverStatusUsingUserId(userId, "offline");

        verify(geoOperations).remove("drivers", "10");
        verify(driverRepository).save(any(Driver.class));
        verify(driverUpdatePublisher).publishDriverUpdate(any());
    }
}
