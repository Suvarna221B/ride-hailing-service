package com.example.ridehailing.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTypeTest {

    @Test
    public void testEnumValues() {
        assertNotNull(UserType.RIDER);
        assertNotNull(UserType.DRIVER);
    }

    @Test
    public void testValueOf() {
        assertEquals(UserType.RIDER, UserType.valueOf("RIDER"));
        assertEquals(UserType.DRIVER, UserType.valueOf("DRIVER"));
    }
}
