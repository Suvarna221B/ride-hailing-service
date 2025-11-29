package com.example.ridehailing.service;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.Payment;
import com.example.ridehailing.service.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final List<PaymentStrategy> paymentStrategies;
    private final RideService rideService;

    public void processPayment(PaymentRequestMessage message) {
        PaymentStrategy strategy = paymentStrategies.stream()
                .filter(s -> s.applies(message.getPaymentMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No payment strategy found for method: " + message.getPaymentMethod()));

        Payment payment = strategy.makePayment(message);

        if ("SUCCESS".equals(payment.getStatus())) {
            rideService.completeRideWithPayment(message.getRideId(), payment.getId());
        }
    }
}
