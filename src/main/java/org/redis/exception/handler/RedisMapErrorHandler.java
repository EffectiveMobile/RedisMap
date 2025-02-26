package org.redis.exception.handler;

import lombok.extern.slf4j.Slf4j;

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
    public <T extends Exception> T handleError(String message, Exception ex, Class<T> exceptionType) throws T {
        log.error(message, ex);

        try {
            throw exceptionType
                    .getConstructor(String.class, Throwable.class)
                    .newInstance(message, ex);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create exception of type: " + exceptionType.getName(), e);
        }
    }
}
