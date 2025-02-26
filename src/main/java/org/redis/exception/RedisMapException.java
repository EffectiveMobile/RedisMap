package org.redis.exception;

/**
 * Base exception class for all Redis-related errors.
 */
public abstract class RedisMapException extends RuntimeException {
    public RedisMapException(String message) {
        super(message);
    }

    public RedisMapException(String message, Throwable cause) {
        super(message, cause);
    }
}
