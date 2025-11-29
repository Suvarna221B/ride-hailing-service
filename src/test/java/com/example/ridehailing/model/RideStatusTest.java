package com.example.ridehailing.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RideStatusTest {

    @Test
    public void testEnumValues() {
        assertNotNull(RideStatus.REQUESTED);
        assertNotNull(RideStatus.ASSIGNED);
        assertNotNull(RideStatus.IN_PROGRESS);
        assertNotNull(RideStatus.PAYMENT_PENDING);
        assertNotNull(RideStatus.COMPLETED);
        assertNotNull(RideStatus.CANCELLED);
    }

    @Test
    public void testValueOf() {
        assertEquals(RideStatus.REQUESTED, RideStatus.valueOf("REQUESTED"));
        assertEquals(RideStatus.ASSIGNED, RideStatus.valueOf("ASSIGNED"));
        assertEquals(RideStatus.IN_PROGRESS, RideStatus.valueOf("IN_PROGRESS"));
        assertEquals(RideStatus.PAYMENT_PENDING, RideStatus.valueOf("PAYMENT_PENDING"));
        assertEquals(RideStatus.COMPLETED, RideStatus.valueOf("COMPLETED"));
        assertEquals(RideStatus.CANCELLED, RideStatus.valueOf("CANCELLED"));
    }
}
