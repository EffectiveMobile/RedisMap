package org.redis.validator;

import org.redis.exception.RedisMapValidationException;
import org.redis.exception.handler.RedisMapErrorHandler;

/**
 * Validator class for Redis map keys.
 */
public class RedisMapKeyValidator {
    private static final RedisMapErrorHandler errorHandler = new RedisMapErrorHandler();

    /**
     * Validates that the provided Redis key is not null or empty.
     *
     * @param key the Redis key to validate.
     * @throws RedisMapValidationException if the key is null or empty.
     */
    public static void validateKeyNotNullOrEmpty(String key) {
        if (key == null || key.trim()
                .isEmpty()) {
            String errorMessage = "Redis key cannot be null or empty. Provided value: " + key;
            errorHandler.handleError(errorMessage,
                    new RedisMapValidationException(errorMessage),
                    RedisMapValidationException.class);
        }
    }
}