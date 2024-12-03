package org.redis;

import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JedisPool jedis = new JedisPool("localhost", 6379);
        RedisMap redisMap = new RedisMap(jedis);

        redisMap.put("1", "value1");
        redisMap.put("2", "value2");


        System.out.println(redisMap.get("1"));
        System.out.println(redisMap.containsKey("2"));
        System.out.println(redisMap.containsValue("value3"));
        System.out.println(redisMap.containsValue("value2"));
        System.out.println(redisMap.isEmpty());


        Map<String, String> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", "value3");

        redisMap.putAll(testMap);


        /*
         redisMap.clear();
         redisMap.remove("1");
        System.out.println(redisMap.keySet()); // [key2]

         */
    }
}