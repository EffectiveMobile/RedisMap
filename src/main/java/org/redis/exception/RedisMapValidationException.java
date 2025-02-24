package org.redis.exception;

/**
 * Exception thrown when a validation error occurs in Redis operations.
 */
public class RedisMapValidationException extends RedisMapException {
    public RedisMapValidationException(String message) {
        super(message);
    }

    public RedisMapValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
