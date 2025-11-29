package com.example.ridehailing.service;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.Payment;
import com.example.ridehailing.model.PaymentMethod;
import com.example.ridehailing.service.strategy.PaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock
    private PaymentStrategy cashStrategy;

    @Mock
    private PaymentStrategy cardStrategy;

    @Mock
    private com.example.ridehailing.service.RideService rideService;

    @BeforeEach
    public void setUp() {
        List<PaymentStrategy> strategies = Arrays.asList(cashStrategy, cardStrategy);
        paymentService = new PaymentService(strategies, rideService);
    }

    @Test
    public void testProcessPayment_Success() {
        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .rideId(1L)
                .userId(10L)
                .paymentAmount(BigDecimal.valueOf(150.0))
                .paymentMethod(PaymentMethod.CASH)
                .build();

        Payment payment = Payment.builder()
                .id(100L)
                .status("SUCCESS")
                .build();

        when(cashStrategy.applies(PaymentMethod.CASH)).thenReturn(true);
        when(cashStrategy.makePayment(message)).thenReturn(payment);

        paymentService.processPayment(message);

        verify(cashStrategy).makePayment(message);
        verify(rideService).completeRideWithPayment(1L, 100L);
        verify(cardStrategy, never()).makePayment(any());
    }

    @Test
    public void testProcessPayment_NoStrategyFound() {
        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .rideId(1L)
                .userId(10L)
                .paymentAmount(BigDecimal.valueOf(150.0))
                .paymentMethod(PaymentMethod.WALLET)
                .build();

        when(cashStrategy.applies(PaymentMethod.WALLET)).thenReturn(false);
        when(cardStrategy.applies(PaymentMethod.WALLET)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(message);
        });
    }
}
