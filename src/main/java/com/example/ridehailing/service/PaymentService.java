package com.example.ridehailing.service;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.Payment;
import com.example.ridehailing.service.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final List<PaymentStrategy> paymentStrategies;
    private final RideService rideService;

    public void processPayment(PaymentRequestMessage message) {
        log.info("Processing payment for ride ID: {}, method: {}", message.getRideId(), message.getPaymentMethod());
        PaymentStrategy strategy = paymentStrategies.stream()
                .filter(s -> s.applies(message.getPaymentMethod()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No payment strategy found for method: {}", message.getPaymentMethod());
                    return new IllegalArgumentException(
                            "No payment strategy found for method: " + message.getPaymentMethod());
                });

        Payment payment = strategy.makePayment(message);
        log.info("Payment processed for ride ID: {}, status: {}", message.getRideId(), payment.getStatus());

        if ("SUCCESS".equals(payment.getStatus())) {
            rideService.completeRideWithPayment(message.getRideId(), payment.getId());
        }
    }
}
