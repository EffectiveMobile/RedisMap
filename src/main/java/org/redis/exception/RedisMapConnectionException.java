package org.redis.exception;

/**
 * Exception thrown when a Redis connection issue occurs.
 */
public class RedisMapConnectionException extends RedisMapException {
    public RedisMapConnectionException(String message) {
        super(message);
    }

    public RedisMapConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
