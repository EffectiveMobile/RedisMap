package org.redis.util.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redis.exception.RedisMapConnectionException;
import org.redis.util.RedisMapConstantUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Singleton for managing Redis connection pool.
 */
@Slf4j
@Getter
public class RedisMapConfig {
    private static final RedisMapConfig INSTANCE = new RedisMapConfig();
    private final JedisPool jedisPool;

    private RedisMapConfig() {
        this.jedisPool = createJedisPool();
    }

    /**
     * Returns the singleton instance.
     */
    public static RedisMapConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Creates and configures a Redis connection pool.
     */
    private JedisPool createJedisPool() {
        try {
            var poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(RedisMapConstantUtil.MAX_TOTAL_CONNECTIONS);
            poolConfig.setMaxIdle(RedisMapConstantUtil.MAX_IDLE_CONNECTIONS);
            poolConfig.setMaxWait(Duration.ofMillis(RedisMapConstantUtil.MAX_WAIT_TIME_MILLIS));
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);

            String redisHost = System.getProperty("REDIS_HOST",
                    System.getenv()
                            .getOrDefault("REDIS_HOST", RedisMapConstantUtil.REDIS_HOST));
            int redisPort = Integer.parseInt(System.getProperty("REDIS_PORT",
                    System.getenv()
                            .getOrDefault("REDIS_PORT", String.valueOf(RedisMapConstantUtil.REDIS_APP_PORT))));

            log.info("Initializing JedisPool with host: {} and port: {}", redisHost,
                    redisPort);

            return new JedisPool(poolConfig, redisHost, redisPort);
        } catch (Exception ex) {
            log.error("Failed to initialize JedisPool", ex);
            throw new RedisMapConnectionException("Failed to initialize JedisPool", ex);
        }
    }
}