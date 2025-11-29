package com.example.ridehailing.service;

import com.example.ridehailing.dto.DriverLocationDto;
import com.example.ridehailing.exception.ValidationException;
import com.example.ridehailing.model.Driver;
import com.example.ridehailing.model.DriverStatus;
import com.example.ridehailing.model.User;
import com.example.ridehailing.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DriverService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverRepository driverRepository;
    private final UserService userService;

    @Value("${redis.ttl.seconds}")
    private long ttlSeconds;

    public DriverService(RedisTemplate<String, Object> redisTemplate,
            DriverRepository driverRepository,
            UserService userService) {
        this.redisTemplate = redisTemplate;
        this.driverRepository = driverRepository;
        this.userService = userService;
    }

    public void updateLocation(Long driverId, DriverLocationDto locationDto) {
        String key = "driverId:" + driverId;
        redisTemplate.opsForValue().set(key, locationDto, ttlSeconds, TimeUnit.SECONDS);
    }

    public void registerDriver(Long userId) {
        if (driverRepository.findByUserId(userId).isPresent()) {
            throw new ValidationException("Driver already registered");
        }

        User user = userService.getUserEntityById(userId);

        Driver driver = Driver.builder()
                .user(user)
                .name(user.getUsername())
                .status(DriverStatus.OFFLINE)
                .build();

        driverRepository.save(driver);
    }

    public void updateDriverStatus(Long userId, String status) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("Driver not found for user"));

        DriverStatus driverStatus = DriverStatus.fromString(status);
        driver.setStatus(driverStatus);
        driverRepository.save(driver);
    }
}
