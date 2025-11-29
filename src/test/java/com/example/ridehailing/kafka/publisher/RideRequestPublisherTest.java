package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.RideRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideRequestPublisherTest {

    @InjectMocks
    private RideRequestPublisher rideRequestPublisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testPublishRideRequest() {
        String requestId = "req-123";
        Long rideId = 1L;
        List<Long> driverIds = Arrays.asList(10L, 20L, 30L);

        rideRequestPublisher.publishRideRequest(requestId, rideId, driverIds);

        ArgumentCaptor<RideRequestMessage> messageCaptor = ArgumentCaptor.forClass(RideRequestMessage.class);
        verify(kafkaTemplate).send(eq("rideRequest"), messageCaptor.capture());

        RideRequestMessage message = messageCaptor.getValue();
        assertEquals(requestId, message.getRequestId());
        assertEquals(rideId, message.getRideId());
        assertEquals(driverIds, message.getDriverIds());
    }
}
