package com.example.ridehailing.service.strategy;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.PaymentMethod;

public interface PaymentStrategy {
    boolean applies(PaymentMethod method);

    com.example.ridehailing.model.Payment makePayment(PaymentRequestMessage message);
}
