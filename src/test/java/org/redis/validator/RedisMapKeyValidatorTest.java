package org.redis.validator;

import org.junit.jupiter.api.Test;
import org.redis.exception.RedisMapValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisMapKeyValidatorTest {

    @Test
    public void testValidateKeyNotNullOrEmptyThrowsExceptionForNullKey() {
        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(null));

        String expectedMessage = "Redis key cannot be null or empty. Provided value: " + null;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateKeyNotNullOrEmptyThrowsExceptionForEmptyKey() {
        String key = "";

        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key));

        String expectedMessage = "Redis key cannot be null or empty. Provided value: " + key;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateKeyNotNullOrEmptyThrowsExceptionForWhitespaceKey() {
        String key = "   ";

        var exception = assertThrows(RedisMapValidationException.class,
                () -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key));

        String expectedMessage = "Redis key cannot be null or empty. Provided value: " + key;
        assertTrue(exception.getMessage()
                .contains(expectedMessage));
    }

    @Test
    public void testValidateKeyNotNullOrEmptyDoesNotThrowExceptionForValidKey() {
        String key = "validKey";

        assertDoesNotThrow(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key));
    }
}