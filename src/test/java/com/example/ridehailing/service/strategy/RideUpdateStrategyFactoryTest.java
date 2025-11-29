package com.example.ridehailing.service.strategy;

import com.example.ridehailing.model.RideUpdateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideUpdateStrategyFactoryTest {

    @Mock
    private AcceptRideStrategy acceptRideStrategy;

    @Mock
    private InProgressRideStrategy inProgressRideStrategy;

    @Mock
    private PaymentPendingRideStrategy paymentPendingRideStrategy;

    @Test
    public void testGetStrategy_Accept() {
        when(acceptRideStrategy.isApplicable(RideUpdateType.ACCEPT)).thenReturn(true);

        RideUpdateStrategyFactory factory = new RideUpdateStrategyFactory(
                Arrays.asList(acceptRideStrategy, inProgressRideStrategy, paymentPendingRideStrategy));

        RideUpdateStrategy strategy = factory.getStrategy(RideUpdateType.ACCEPT);
        assertEquals(acceptRideStrategy, strategy);
    }

    @Test
    public void testGetStrategy_InProgress() {
        when(inProgressRideStrategy.isApplicable(RideUpdateType.IN_PROGRESS)).thenReturn(true);

        RideUpdateStrategyFactory factory = new RideUpdateStrategyFactory(
                Arrays.asList(acceptRideStrategy, inProgressRideStrategy, paymentPendingRideStrategy));

        RideUpdateStrategy strategy = factory.getStrategy(RideUpdateType.IN_PROGRESS);
        assertEquals(inProgressRideStrategy, strategy);
    }

    @Test
    public void testGetStrategy_PaymentPending() {
        when(paymentPendingRideStrategy.isApplicable(RideUpdateType.PAYMENT_PENDING)).thenReturn(true);

        RideUpdateStrategyFactory factory = new RideUpdateStrategyFactory(
                Arrays.asList(acceptRideStrategy, inProgressRideStrategy, paymentPendingRideStrategy));

        RideUpdateStrategy strategy = factory.getStrategy(RideUpdateType.PAYMENT_PENDING);
        assertEquals(paymentPendingRideStrategy, strategy);
    }

    @Test
    public void testGetStrategy_NotFound() {
        when(acceptRideStrategy.isApplicable(RideUpdateType.COMPLETED)).thenReturn(false);
        when(inProgressRideStrategy.isApplicable(RideUpdateType.COMPLETED)).thenReturn(false);
        when(paymentPendingRideStrategy.isApplicable(RideUpdateType.COMPLETED)).thenReturn(false);

        RideUpdateStrategyFactory factory = new RideUpdateStrategyFactory(
                Arrays.asList(acceptRideStrategy, inProgressRideStrategy, paymentPendingRideStrategy));

        assertThrows(IllegalArgumentException.class, () -> {
            factory.getStrategy(RideUpdateType.COMPLETED);
        });
    }

    @Test
    public void testGetStrategy_EmptyList() {
        RideUpdateStrategyFactory factory = new RideUpdateStrategyFactory(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> {
            factory.getStrategy(RideUpdateType.ACCEPT);
        });
    }
}
