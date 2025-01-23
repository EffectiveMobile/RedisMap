package org.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Function;

public class RedisExecutor {
    private final JedisPool jedisPool;

    public RedisExecutor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <R> R execute(Function<Jedis, R> function) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return function.apply(jedis);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
}

