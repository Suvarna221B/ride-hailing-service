package com.example.ridehailing.controller;

import com.example.ridehailing.dto.RideRequestDto;
import com.example.ridehailing.dto.RideResponseDto;
import com.example.ridehailing.model.RideStatus;
import com.example.ridehailing.service.RideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    @MockBean
    private com.example.ridehailing.service.AuthService authService;

    @MockBean
    private com.example.ridehailing.aspect.RoleAuthorizationAspect roleAuthorizationAspect;

    @Test
    public void testCreateRide_Success() throws Exception {
        RideRequestDto request = RideRequestDto.builder()
                .userId(1L)
                .startLatitude(12.9716)
                .startLongitude(77.5946)
                .destLatitude(12.8797)
                .destLongitude(77.6850)
                .build();

        RideResponseDto response = RideResponseDto.builder()
                .rideId(100L)
                .status(RideStatus.REQUESTED)
                .fare(BigDecimal.valueOf(150.0))
                .build();

        when(rideService.createRide(any(RideRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/rides")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").value(100))
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.fare").value(150.0));
    }

    @Test
    public void testAcceptRide_Success() throws Exception {
        Long rideId = 1L;
        Long driverId = 20L;

        mockMvc.perform(post("/api/rides/" + rideId + "/accept")
                .param("driverId", String.valueOf(driverId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
