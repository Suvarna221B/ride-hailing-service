package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.PaymentRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentRequestPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentRequest(Long rideId, Long userId, java.math.BigDecimal paymentAmount) {
        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .rideId(rideId)
                .userId(userId)
                .paymentAmount(paymentAmount)
                .build();
        kafkaTemplate.send("paymentRequest", message);
    }
}
