package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.DriverUpdateMessage;
import com.example.ridehailing.model.DriverStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverUpdatePublisherTest {

    @InjectMocks
    private DriverUpdatePublisher publisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testPublishDriverUpdate_Success() {
        DriverUpdateMessage message = DriverUpdateMessage.builder()
                .driverId(123L)
                .status(DriverStatus.AVAILABLE)
                .build();

        publisher.publishDriverUpdate(message);

        verify(kafkaTemplate).send("driverupdate", message);
    }

    @Test
    public void testPublishDriverUpdate_KafkaFailure() {
        DriverUpdateMessage message = DriverUpdateMessage.builder()
                .driverId(123L)
                .status(DriverStatus.BUSY)
                .build();

        when(kafkaTemplate.send("driverupdate", message))
                .thenThrow(new RuntimeException("Kafka error"));

        assertThrows(RuntimeException.class, () -> {
            publisher.publishDriverUpdate(message);
        });

        verify(kafkaTemplate).send("driverupdate", message);
    }
}
