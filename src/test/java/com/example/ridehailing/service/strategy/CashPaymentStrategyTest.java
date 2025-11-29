package com.example.ridehailing.service.strategy;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.Payment;
import com.example.ridehailing.model.PaymentMethod;
import com.example.ridehailing.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashPaymentStrategyTest {

    @InjectMocks
    private CashPaymentStrategy cashPaymentStrategy;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    public void testApplies() {
        assertTrue(cashPaymentStrategy.applies(PaymentMethod.CASH));
    }

    @Test
    public void testMakePayment() {
        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .rideId(1L)
                .userId(10L)
                .paymentAmount(BigDecimal.valueOf(150.0))
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Payment savedPayment = Payment.builder()
                .id(100L)
                .rideId(1L)
                .amount(BigDecimal.valueOf(150.0))
                .paymentMethod(PaymentMethod.CASH)
                .status("SUCCESS")
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment result = cashPaymentStrategy.makePayment(message);

        verify(paymentRepository).save(any(Payment.class));
        assertEquals(100L, result.getId());
        assertEquals("SUCCESS", result.getStatus());
    }
}
