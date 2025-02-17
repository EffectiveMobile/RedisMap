package org.redis.util.config;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.redis.exception.RedisMapConnectionException;
import org.redis.util.RedisMapConstantUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Utility class for configuring Redis connection using Jedis.
 */
@Slf4j
@UtilityClass
public class RedisMapConfig {
    private static final JedisPool jedisPool;

    static {
        try {
            var poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(RedisMapConstantUtil.MAX_TOTAL_CONNECTIONS);
            poolConfig.setMaxIdle(RedisMapConstantUtil.MAX_IDLE_CONNECTIONS);
            poolConfig.setMaxWait(Duration.ofMillis(RedisMapConstantUtil.MAX_WAIT_TIME_MILLIS));
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);

            jedisPool = new JedisPool(poolConfig, RedisMapConstantUtil.REDIS_HOST, RedisMapConstantUtil.REDIS_PORT);
            log.info("JedisPool initialized successfully with host: {} and port: {}", RedisMapConstantUtil.REDIS_HOST,
                    RedisMapConstantUtil.REDIS_PORT);
        } catch (Exception ex) {
            log.error("Failed to initialize JedisPool", ex);
            throw new RedisMapConnectionException("Failed to initialize JedisPool", ex);
        }
    }

    /**
     * Retrieves the initialized Jedis connection pool.
     *
     * @return JedisPool instance.
     * @throws RedisMapConnectionException if the connection pool is not initialized.
     */
    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            throw new RedisMapConnectionException("JedisPool is not initialized");
        }
        return jedisPool;
    }
}
