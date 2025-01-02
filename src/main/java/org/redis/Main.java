package org.redis;

import redis.clients.jedis.JedisPool;

public class Main {
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool("localhost", 6379);

        RedisMap redisMap = new RedisMap(jedisPool, 0);

        redisMap.put("key", "value");
        redisMap.put("key2", "value2");

        System.out.println(redisMap.size());
        System.out.println(redisMap.remove("key"));
        System.out.println(redisMap.size());
    }
}