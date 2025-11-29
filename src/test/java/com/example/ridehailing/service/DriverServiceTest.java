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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void testUpdateLocation() {
        Long driverId = 1L;
        DriverLocationDto locationDto = DriverLocationDto.builder()
                .latitude(12.9716)
                .longitude(77.5946)
                .status(DriverStatus.AVAILABLE)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        driverService.updateLocation(driverId, locationDto);

        verify(valueOperations).set(eq("driverId:1"), eq(locationDto), anyLong(), any());
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

        driverService.updateDriverStatus(userId, "available");

        verify(driverRepository).save(any(Driver.class));
    }
}
