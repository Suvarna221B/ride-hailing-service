package com.example.ridehailing.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DriverStatusTest {

    @Test
    public void testFromString_Available() {
        assertEquals(DriverStatus.AVAILABLE, DriverStatus.fromString("AVAILABLE"));
        assertEquals(DriverStatus.AVAILABLE, DriverStatus.fromString("available"));
        assertEquals(DriverStatus.AVAILABLE, DriverStatus.fromString("Available"));
    }

    @Test
    public void testFromString_Busy() {
        assertEquals(DriverStatus.BUSY, DriverStatus.fromString("BUSY"));
        assertEquals(DriverStatus.BUSY, DriverStatus.fromString("busy"));
    }

    @Test
    public void testFromString_Offline() {
        assertEquals(DriverStatus.OFFLINE, DriverStatus.fromString("OFFLINE"));
        assertEquals(DriverStatus.OFFLINE, DriverStatus.fromString("offline"));
    }

    @Test
    public void testFromString_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            DriverStatus.fromString("INVALID");
        });
    }

    @Test
    public void testFromString_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            DriverStatus.fromString(null);
        });
    }
}
