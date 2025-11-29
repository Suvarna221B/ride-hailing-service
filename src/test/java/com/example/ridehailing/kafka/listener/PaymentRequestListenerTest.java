package com.example.ridehailing.kafka.listener;

import com.example.ridehailing.dto.PaymentRequestMessage;
import com.example.ridehailing.model.PaymentMethod;
import com.example.ridehailing.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentRequestListenerTest {

    @InjectMocks
    private PaymentRequestListener listener;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void testHandlePaymentRequest_Success() throws Exception {
        String messageJson = "{\"rideId\":1,\"userId\":10,\"paymentAmount\":150.0,\"paymentMethod\":\"CASH\"}";

        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .rideId(1L)
                .userId(10L)
                .paymentAmount(BigDecimal.valueOf(150.0))
                .paymentMethod(PaymentMethod.CASH)
                .build();

        when(objectMapper.readValue(messageJson, PaymentRequestMessage.class)).thenReturn(message);

        listener.handlePaymentRequest(messageJson);

        verify(objectMapper).readValue(messageJson, PaymentRequestMessage.class);
        verify(paymentService).processPayment(message);
    }

    @Test
    public void testHandlePaymentRequest_InvalidJson() throws Exception {
        String invalidJson = "{invalid}";

        when(objectMapper.readValue(eq(invalidJson), eq(PaymentRequestMessage.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Invalid JSON") {
                });

        assertThrows(RuntimeException.class, () -> {
            listener.handlePaymentRequest(invalidJson);
        });

        verify(paymentService, never()).processPayment(any());
    }
}
