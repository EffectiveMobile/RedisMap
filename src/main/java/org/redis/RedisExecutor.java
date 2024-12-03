package org.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Function;

public class RedisExecutor {
    private final JedisPool jedisPool;

    public RedisExecutor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public  <R> R execute(Function<Jedis, R> function) {
        try (Jedis jedis = jedisPool.getResource()) {
            return function.apply(jedis);
        }
    }

}
