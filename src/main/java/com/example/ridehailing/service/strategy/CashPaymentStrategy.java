package com.example.ridehailing.service.strategy;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.Payment;
import com.example.ridehailing.model.PaymentMethod;
import com.example.ridehailing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean applies(PaymentMethod method) {
        return PaymentMethod.CASH == method;
    }

    @Override
    @Transactional
    public Payment makePayment(PaymentRequestMessage message) {
        // can add further strategy based on required payment service providers
        // integration to the PSP would be made from here
        Payment payment = Payment.builder()
                .rideId(message.getRideId())
                .amount(message.getPaymentAmount())
                .paymentMethod(PaymentMethod.CASH)
                .status("SUCCESS")
                .transactionId(UUID.randomUUID().toString()) //this could be the id corresponding to the transaction in PSP
                .paymentTime(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }
}
