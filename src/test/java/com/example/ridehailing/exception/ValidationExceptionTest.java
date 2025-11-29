package com.example.ridehailing.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Validation failed";
        ValidationException exception = new ValidationException(message);
        assertEquals(message, exception.getMessage());
    }
}
