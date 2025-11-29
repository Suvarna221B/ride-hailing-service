package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.RideUpdateMessage;
import com.example.ridehailing.model.RideStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RideUpdatePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RideUpdatePublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRideUpdate(Long rideId, Long userId, RideStatus status) {
        RideUpdateMessage message = RideUpdateMessage.builder()
                .rideId(rideId)
                .userId(userId)
                .status(status)
                .build();
        kafkaTemplate.send("rideUpdates", message);
    }

    public void publishRideUpdate(Long rideId, Long userId, RideStatus status, BigDecimal fare) {
        RideUpdateMessage message = RideUpdateMessage.builder()
                .rideId(rideId)
                .userId(userId)
                .status(status)
                .fare(fare)
                .build();
        kafkaTemplate.send("rideUpdates", message);
    }
}
