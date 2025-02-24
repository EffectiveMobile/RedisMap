package org.redis.exception;

/**
 * Exception thrown when a Redis operation fails.
 */
public class RedisMapOperationException extends RedisMapException {
    public RedisMapOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
