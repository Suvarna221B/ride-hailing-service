package com.example.ridehailing.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RideUpdateTypeTest {

    @Test
    public void testEnumValues() {
        assertNotNull(RideUpdateType.ACCEPT);
        assertNotNull(RideUpdateType.IN_PROGRESS);
        assertNotNull(RideUpdateType.PAYMENT_PENDING);
        assertNotNull(RideUpdateType.COMPLETED);
    }

    @Test
    public void testValueOf() {
        assertEquals(RideUpdateType.ACCEPT, RideUpdateType.valueOf("ACCEPT"));
        assertEquals(RideUpdateType.IN_PROGRESS, RideUpdateType.valueOf("IN_PROGRESS"));
        assertEquals(RideUpdateType.PAYMENT_PENDING, RideUpdateType.valueOf("PAYMENT_PENDING"));
        assertEquals(RideUpdateType.COMPLETED, RideUpdateType.valueOf("COMPLETED"));
    }
}
