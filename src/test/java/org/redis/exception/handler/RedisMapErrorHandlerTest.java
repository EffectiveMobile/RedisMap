package org.redis.exception.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redis.exception.RedisMapConnectionException;
import org.redis.exception.RedisMapOperationException;
import org.redis.exception.RedisMapValidationException;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedisMapErrorHandlerTest {
    private RedisMapErrorHandler errorHandler;
    private TestLogger testLogger;

    @BeforeEach
    public void setUp() {
        errorHandler = new RedisMapErrorHandler();
        testLogger = TestLoggerFactory.getTestLogger(RedisMapErrorHandler.class);
        TestLoggerFactory.clear();
    }

    @Test
    public void testHandleRedisMapOperationException() {
        String message = "Operation error occurred";
        var ex = new Exception("Test exception");

        assertThrows(RedisMapOperationException.class,
                () -> errorHandler.handleError(message, ex, RedisMapOperationException.class));

        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        assertEquals(1, loggingEvents.size(), "Expected one logging event");

        LoggingEvent actualEvent = loggingEvents.get(0);
        assertEquals("ERROR", actualEvent.getLevel()
                .toString(), "Log level should be ERROR");
        assertEquals(message, actualEvent.getMessage(), "Log message should match");
    }

    @Test
    public void testHandleRedisMapConnectionException() {
        String message = "Connection error";
        var ex = new Exception("Test exception");

        assertThrows(RedisMapConnectionException.class,
                () -> errorHandler.handleError(message, ex, RedisMapConnectionException.class),
                "Expected a RedisMapConnectionException to be thrown");

        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        assertEquals(1, loggingEvents.size(), "Expected one logging event");

        LoggingEvent actualEvent = loggingEvents.get(0);
        assertEquals("ERROR", actualEvent.getLevel()
                .toString(), "Log level should be ERROR");
        assertEquals(message, actualEvent.getMessage(), "Log message should match");
    }

    @Test
    public void testHandleRedisMapValidationException() {
        String message = "Validation error";
        var ex = new Exception("Test exception");

        assertThrows(RedisMapValidationException.class,
                () -> errorHandler.handleError(message, ex, RedisMapValidationException.class),
                "Expected a RedisMapValidationException to be thrown");

        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        assertEquals(1, loggingEvents.size(), "Expected one logging event");

        LoggingEvent actualEvent = loggingEvents.get(0);
        assertEquals("ERROR", actualEvent.getLevel()
                .toString(), "Log level should be ERROR");
        assertEquals(message, actualEvent.getMessage(), "Log message should match");
    }

    @Test
    public void testHandleRuntimeException() {
        String message = "Unknown error";
        var ex = new Exception("Test exception");

        assertThrows(RuntimeException.class, () -> errorHandler.handleError(message, ex, RuntimeException.class),
                "Expected a RuntimeException to be thrown");

        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        assertEquals(1, loggingEvents.size(), "Expected one logging event");

        LoggingEvent actualEvent = loggingEvents.get(0);
        assertEquals("ERROR", actualEvent.getLevel()
                .toString(), "Log level should be ERROR");
        assertEquals(message, actualEvent.getMessage(), "Log message should match");
    }
}