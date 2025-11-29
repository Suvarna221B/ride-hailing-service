package com.example.ridehailing.kafka.listener;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "paymentRequest", groupId = "payment-group")
    public void handlePaymentRequest(String messageJson) {
        try {
            PaymentRequestMessage message = objectMapper.readValue(messageJson, PaymentRequestMessage.class);
            log.info("Received payment request for ride: {}", message.getRideId());
            paymentService.processPayment(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse payment request message: {}", messageJson, e);
            throw new RuntimeException("Invalid payment request message", e);
        }
    }
}
