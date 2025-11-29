package com.example.ridehailing.controller;

import com.example.ridehailing.config.SecurityConfig;
import com.example.ridehailing.dto.DriverLocationDto;
import com.example.ridehailing.dto.UserDto;
import com.example.ridehailing.exception.GlobalExceptionHandler;
import com.example.ridehailing.model.DriverStatus;
import com.example.ridehailing.model.UserType;
import com.example.ridehailing.service.AuthService;
import com.example.ridehailing.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
public class DriverControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DriverService driverService;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void testUpdateLocation_Success() throws Exception {
                Long driverId = 1L;
                DriverLocationDto locationDto = new DriverLocationDto();
                locationDto.setLatitude(12.9716);
                locationDto.setLongitude(77.5946);

                // Mocking authService is not needed for this specific test based on the
                // provided snippet
                // UserDto userDto = UserDto.builder()
                // .id(1L)
                // .username("driver1")
                // .userType(UserType.DRIVER)
                // .build();
                // when(authService.validateToken(any())).thenReturn(userDto);

                mockMvc.perform(put("/api/drivers/" + driverId + "/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(locationDto)))
                                .andExpect(status().isOk());

                verify(driverService).updateLocation(eq(driverId), any(DriverLocationDto.class));
        }

        @Test
        public void testRegisterDriver_Success() throws Exception {
                UserDto userDto = UserDto.builder()
                                .id(1L)
                                .username("driver1")
                                .userType(UserType.DRIVER)
                                .build();

                when(authService.validateToken(any())).thenReturn(userDto);

                mockMvc.perform(post("/api/drivers/register")
                                .with(csrf())
                                .header("Authorization", "Bearer valid.token"))
                                .andExpect(status().isOk());

                verify(driverService).registerDriver(1L);
        }

        @Test
        public void testUpdateDriverStatus_Success() throws Exception {
                mockMvc.perform(patch("/api/drivers/status/1")
                                .with(csrf())
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("\"AVAILABLE\""))
                                .andExpect(status().isOk());

                verify(driverService).updateDriverStatusUsingUserId(eq(1L), eq("\"AVAILABLE\""));
        }
}
