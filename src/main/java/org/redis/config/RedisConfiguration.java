package org.redis.config;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfiguration {

    private static final JedisPool JEDIS_POOL;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JEDIS_POOL = new JedisPool(poolConfig, "localhost", 6379);
    }

    public static JedisPool getJedisPool() {
        return JEDIS_POOL;
    }
}
