package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.RideUpdateMessage;
import com.example.ridehailing.model.RideStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideUpdatePublisherTest {

    @InjectMocks
    private RideUpdatePublisher rideUpdatePublisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testPublishRideUpdate_WithoutFare() {
        Long rideId = 1L;
        Long userId = 10L;
        RideStatus status = RideStatus.ASSIGNED;

        rideUpdatePublisher.publishRideUpdate(rideId, userId, status);

        ArgumentCaptor<RideUpdateMessage> messageCaptor = ArgumentCaptor.forClass(RideUpdateMessage.class);
        verify(kafkaTemplate).send(eq("rideUpdates"), messageCaptor.capture());

        RideUpdateMessage message = messageCaptor.getValue();
        assertEquals(rideId, message.getRideId());
        assertEquals(userId, message.getUserId());
        assertEquals(status, message.getStatus());
        assertNull(message.getFare());
    }

    @Test
    public void testPublishRideUpdate_WithFare() {
        Long rideId = 1L;
        Long userId = 10L;
        RideStatus status = RideStatus.PAYMENT_PENDING;
        BigDecimal fare = BigDecimal.valueOf(150.0);

        rideUpdatePublisher.publishRideUpdate(rideId, userId, status, fare);

        ArgumentCaptor<RideUpdateMessage> messageCaptor = ArgumentCaptor.forClass(RideUpdateMessage.class);
        verify(kafkaTemplate).send(eq("rideUpdates"), messageCaptor.capture());

        RideUpdateMessage message = messageCaptor.getValue();
        assertEquals(rideId, message.getRideId());
        assertEquals(userId, message.getUserId());
        assertEquals(status, message.getStatus());
        assertEquals(fare, message.getFare());
    }
}
