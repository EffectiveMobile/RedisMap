package org.redis.validator;

import org.redis.exception.RedisMapValidationException;
import org.redis.exception.handler.RedisMapErrorHandler;

/**
 * Validator class for Redis map values.
 */
public class RedisMapValueValidator {
    private static final RedisMapErrorHandler errorHandler = new RedisMapErrorHandler();

    /**
     * Validates that the provided Redis value is not null or empty.
     *
     * @param value the Redis value to validate.
     * @throws RedisMapValidationException if the value is null or empty.
     */
    public static void validateValueNotNullOrEmpty(String value) {
        if (value == null || value.trim()
                .isEmpty()) {
            String errorMessage = "Redis value cannot be null or empty. Provided value: " + value;
            errorHandler.handleError(errorMessage,
                    new RedisMapValidationException(errorMessage),
                    RedisMapValidationException.class);
        }
    }
}
