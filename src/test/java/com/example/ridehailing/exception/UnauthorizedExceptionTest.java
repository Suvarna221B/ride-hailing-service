package com.example.ridehailing.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnauthorizedExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Unauthorized access";
        UnauthorizedException exception = new UnauthorizedException(message);
        assertEquals(message, exception.getMessage());
    }
}
