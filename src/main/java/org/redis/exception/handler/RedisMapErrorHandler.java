package org.redis.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.redis.exception.RedisMapConnectionException;
import org.redis.exception.RedisMapOperationException;
import org.redis.exception.RedisMapValidationException;

/**
 * Handles Redis-related errors by logging and mapping exceptions to specific types.
 */
@Slf4j
public class RedisMapErrorHandler {

    /**
     * Logs an error message and throws an appropriate Redis-related exception.
     *
     * @param message       The error message.
     * @param ex            The original exception.
     * @param exceptionType The target exception class to be thrown.
     * @param <T>           The type of exception.
     * @throws T The mapped exception.
     */
    public <T extends Exception> void handleError(String message, Exception ex, Class<T> exceptionType) throws T {
        log.error(message, ex);

        Exception mappedException = switch (exceptionType.getSimpleName()) {
            case "RedisConnectionException" -> new RedisMapConnectionException(message, ex);
            case "RedisOperationException" -> new RedisMapOperationException(message, ex);
            case "RedisValidationException" -> new RedisMapValidationException(message, ex);
            default -> new RuntimeException(message, ex);
        };

        throw exceptionType.cast(mappedException);
    }
}
