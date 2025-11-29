package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.DriverUpdateMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverUpdatePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDriverUpdate(DriverUpdateMessage message) {
        try {
            kafkaTemplate.send("driverupdate", message);
            log.info("Published driver update for driver: {}, status: {}",
                    message.getDriverId(), message.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish driver update", e);
        }
    }
}
