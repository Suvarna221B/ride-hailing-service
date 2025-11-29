package com.example.ridehailing.controller;

import com.example.ridehailing.dto.FareRequestDto;
import com.example.ridehailing.dto.FareResponseDto;
import com.example.ridehailing.service.FareCalculationService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FareController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FareControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private FareCalculationService fareCalculationService;

        @MockBean
        private com.example.ridehailing.service.AuthService authService;

        @MockBean
        private com.example.ridehailing.aspect.RoleAuthorizationAspect roleAuthorizationAspect;

        @Test
        public void testCalculateFare_Success() throws Exception {
                FareRequestDto request = FareRequestDto.builder()
                                .startLatitude(12.9716)
                                .startLongitude(77.5946)
                                .destinationLatitude(12.8797)
                                .destinationLongitude(77.6850)
                                .build();

                FareResponseDto response = FareResponseDto.builder()
                                .distanceKm(BigDecimal.valueOf(10.5))
                                .baseFare(BigDecimal.valueOf(147.0))
                                .timeSurcharge(BigDecimal.ZERO)
                                .totalFare(BigDecimal.valueOf(147.0))
                                .build();

                when(fareCalculationService.calculateFare(any(FareRequestDto.class))).thenReturn(response);

                mockMvc.perform(post("/api/fare/calculate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.distanceKm").value(10.5))
                                .andExpect(jsonPath("$.baseFare").value(147.0))
                                .andExpect(jsonPath("$.timeSurcharge").value(0.0))
                                .andExpect(jsonPath("$.totalFare").value(147.0));
        }

        @Test
        public void testCalculateFare_WithTime() throws Exception {
                FareRequestDto request = FareRequestDto.builder()
                                .startLatitude(12.9716)
                                .startLongitude(77.5946)
                                .destinationLatitude(12.8797)
                                .destinationLongitude(77.6850)
                                .actualTimeMinutes(45)
                                .build();

                FareResponseDto response = FareResponseDto.builder()
                                .distanceKm(BigDecimal.valueOf(10.5))
                                .baseFare(BigDecimal.valueOf(147.0))
                                .timeSurcharge(BigDecimal.valueOf(36.75))
                                .totalFare(BigDecimal.valueOf(183.75))
                                .build();

                when(fareCalculationService.calculateFare(any(FareRequestDto.class))).thenReturn(response);

                mockMvc.perform(post("/api/fare/calculate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.distanceKm").value(10.5))
                                .andExpect(jsonPath("$.baseFare").value(147.0))
                                .andExpect(jsonPath("$.timeSurcharge").value(36.75))
                                .andExpect(jsonPath("$.totalFare").value(183.75));
        }
}
