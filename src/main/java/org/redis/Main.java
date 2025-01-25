package org.redis;

import org.redis.map.RedisMap;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Main {
    public static void main(String[] args) {
        final JedisPoolConfig poolConfig = buildPoolConfig();
        try (JedisPool jedisPool = new JedisPool(poolConfig)) {
            RedisMap redisMap = new RedisMap(jedisPool);
            redisMap.clear();
            redisMap.put("1", "one");
            redisMap.put("2", "two");
            System.out.println(redisMap.put("3", "three"));
            System.out.println(redisMap.get("1"));
        }
    }

    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}