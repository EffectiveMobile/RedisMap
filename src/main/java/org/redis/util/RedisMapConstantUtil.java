package org.redis.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class containing Redis-related constants.
 */
@UtilityClass
public class RedisMapConstantUtil {
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_APP_PORT = 6379;
    public static final int REDIS_TEST_PORT = 6380;

    public static final int MAX_TOTAL_CONNECTIONS = 128;
    public static final int MAX_IDLE_CONNECTIONS = 32;
    public static final long MAX_WAIT_TIME_MILLIS = 2000; // 2 се
}
