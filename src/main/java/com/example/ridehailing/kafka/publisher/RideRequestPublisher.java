package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.RideRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideRequestPublisher {

    private final KafkaTemplate<String, RideRequestMessage> kafkaTemplate;

    public RideRequestPublisher(KafkaTemplate<String, RideRequestMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRideRequest(String requestId, Long rideId, List<Long> driverIds) {
        RideRequestMessage message = RideRequestMessage.builder()
                .requestId(requestId)
                .rideId(rideId)
                .driverIds(driverIds)
                .build();
        kafkaTemplate.send("rideRequest", message);
    }
}
