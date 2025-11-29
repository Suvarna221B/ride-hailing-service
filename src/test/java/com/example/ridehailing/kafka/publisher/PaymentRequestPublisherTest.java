package com.example.ridehailing.kafka.publisher;

import com.example.ridehailing.dto.PaymentRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentRequestPublisherTest {

    @InjectMocks
    private PaymentRequestPublisher paymentRequestPublisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testPublishPaymentRequest() {
        Long rideId = 1L;
        Long userId = 10L;
        BigDecimal paymentAmount = BigDecimal.valueOf(150.0);

        paymentRequestPublisher.publishPaymentRequest(rideId, userId, paymentAmount);

        ArgumentCaptor<PaymentRequestMessage> messageCaptor = ArgumentCaptor.forClass(PaymentRequestMessage.class);
        verify(kafkaTemplate).send(eq("paymentRequest"), messageCaptor.capture());

        PaymentRequestMessage message = messageCaptor.getValue();
        assertEquals(rideId, message.getRideId());
        assertEquals(userId, message.getUserId());
        assertEquals(paymentAmount, message.getPaymentAmount());
    }
}
