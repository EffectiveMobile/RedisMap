package org.redis.demo;

import org.redis.map.RedisMap;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

public class Demo {
    public void run() {
        JedisPool jedis = new JedisPool("localhost", 6379);
        RedisMap redisMap = new RedisMap(jedis);

        System.out.println("isEmpty():" + redisMap.isEmpty());
        System.out.println("size():" + redisMap.size());
        System.out.println("values():" + redisMap.values());

        System.out.println("put(\"1\", \"value1\"):" + redisMap.put("1", "value1"));
        System.out.println("put(\"2\", \"value2\"):" + redisMap.put("2", "value2"));

        System.out.println("isEmpty():" + redisMap.isEmpty());
        System.out.println("size():" + redisMap.size());
        System.out.println("values():" + redisMap.values());
        System.out.println("keySet():" + redisMap.keySet());
        System.out.println("get(\"1\"):" + redisMap.get("1"));
        System.out.println("containsKey(\"2\"):" + redisMap.containsKey("2"));
        System.out.println("containsValue(\"some\"):" + redisMap.containsValue("value3"));
        System.out.println("containsValue(\"value2\"):" + redisMap.containsValue("value2"));
        System.out.println("entrySet():" + redisMap.entrySet());

        System.out.println("remove(\"1\"):" + redisMap.remove("1"));

        System.out.println("size():" + redisMap.size());
        System.out.println("containsKey(\"1\"):" + redisMap.containsKey("1"));

        System.out.println("clear.");
        redisMap.clear();

        System.out.println("isEmpty():" + redisMap.isEmpty());
        System.out.println("size():" + redisMap.size());

        System.out.println("put test map.");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", "value3");
        redisMap.putAll(testMap);

        System.out.println("isEmpty():" + redisMap.isEmpty());
        System.out.println("size():" + redisMap.size());
        System.out.println("keySet():" + redisMap.keySet());
    }
}
