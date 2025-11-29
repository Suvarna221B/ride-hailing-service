package com.example.ridehailing.service;

import com.example.ridehailing.dto.DriverLocationDto;
import com.example.ridehailing.dto.DriverUpdateMessage;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.kafka.publisher.DriverUpdatePublisher;
import com.example.ridehailing.model.Driver;
import com.example.ridehailing.model.DriverStatus;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DriverService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverRepository driverRepository;
    private final UserService userService;
    private final DriverUpdatePublisher driverUpdatePublisher;

    @Value("${redis.ttl.seconds}")
    private long ttlSeconds;

    public DriverService(RedisTemplate<String, Object> redisTemplate,
            DriverRepository driverRepository,
            UserService userService,
            DriverUpdatePublisher driverUpdatePublisher) {
        this.redisTemplate = redisTemplate;
        this.driverRepository = driverRepository;
        this.userService = userService;
        this.driverUpdatePublisher = driverUpdatePublisher;
    }

    public void updateLocation(Long userId, DriverLocationDto locationDto) {
        log.info("Updating location for user {}: lat={}, lon={}", userId, locationDto.getLatitude(),
                locationDto.getLongitude());
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Driver not found for user ID: {}", userId);
                    return new ValidationException("Driver not found");
                });
        Point point = new Point(locationDto.getLongitude(), locationDto.getLatitude());
        redisTemplate.opsForGeo().add("drivers:geo", point, driver.getId());
    }

    public List<Long> findNearbyDrivers(double latitude, double longitude, double radiusKm) {
        log.info("Finding nearby drivers at lat={}, lon={}, radius={}km", latitude, longitude, radiusKm);
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo()
                .radius("drivers:geo", circle);

        List<Long> driverIds = new ArrayList<>();
        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : results) {
                Object content = result.getContent().getName();
                if (content instanceof Long) {
                    driverIds.add((Long) content);
                }
            }
        }
        log.info("Found {} nearby drivers", driverIds.size());
        return driverIds;
    }

    public void registerDriver(Long userId) {
        log.info("Registering driver for user ID: {}", userId);
        if (driverRepository.findByUserId(userId).isPresent()) {
            log.warn("Driver already registered for user ID: {}", userId);
            throw new ValidationException("Driver already registered");
        }

        User user = userService.getUserEntityById(userId);

        Driver driver = Driver.builder()
                .user(user)
                .name(user.getUsername())
                .status(DriverStatus.OFFLINE)
                .build();

        driverRepository.save(driver);
        log.info("Driver registered successfully with ID: {}", driver.getId());
    }

    public void updateDriverStatusUsingDriverId(Long driverId, String status) {
        log.info("Updating status for driver ID: {} to {}", driverId, status);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> {
                    log.error("Driver not found with ID: {}", driverId);
                    return new ValidationException("Driver not found for user");
                });
        updateDriverStatus(status, driver);
    }

    public void updateDriverStatusUsingUserId(Long userId, String statusUpdate) {
        log.info("Updating status for user ID: {} to {}", userId, statusUpdate);
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Driver not found for user ID: {}", userId);
                    return new ValidationException("Driver not found for user");
                });
        updateDriverStatus(statusUpdate, driver);
    }

    private void updateDriverStatus(String statusUpdate, Driver driver) {
        DriverStatus driverStatus = DriverStatus.fromString(statusUpdate);
        log.info("Changing driver {} status from {} to {}", driver.getId(), driver.getStatus(), driverStatus);

        switch (driverStatus) {
            case AVAILABLE -> {
                // No specific Redis action for AVAILABLE here, handled by updateLocation
            }
            case OFFLINE, BUSY -> {
                log.info("Removing driver {} from Redis geo index due to status {}", driver.getId(), driverStatus);
                redisTemplate.opsForGeo().remove("drivers", driver.getId().toString());
            }
            default -> {
                // No Redis modification for other statuses
            }
        }

        driver.setStatus(driverStatus);
        driverRepository.save(driver);

        driverUpdatePublisher.publishDriverUpdate(
                DriverUpdateMessage.builder()
                        .driverId(driver.getId())
                        .status(driverStatus)
                        .build());
    }
}
