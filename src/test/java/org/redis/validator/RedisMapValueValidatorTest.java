package org.redis.validator;

import org.junit.jupiter.api.Test;
import org.redis.exception.RedisMapValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisMapValueValidatorTest {

    @Test
    public void testValidateValueNotNullOrEmptyThrowsExceptionForNullValue() {
        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapValueValidator.validateValueNotNullOrEmpty(null));

        String expectedMessage = "Redis value cannot be null or empty. Provided value: " + null;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateValueNotNullOrEmptyThrowsExceptionForEmptyValue() {
        String value = "";

        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapValueValidator.validateValueNotNullOrEmpty(value));

        String expectedMessage = "Redis value cannot be null or empty. Provided value: " + value;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateValueNotNullOrEmptyThrowsExceptionForWhitespaceValue() {
        String value = "   ";

        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapValueValidator.validateValueNotNullOrEmpty(value));

        String expectedMessage = "Redis value cannot be null or empty. Provided value: " + value;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateValueNotNullOrEmptyDoesNotThrowExceptionForValidValue() {
        String value = "validValue";

        assertDoesNotThrow(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value));
    }
}