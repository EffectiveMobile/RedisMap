package org.redis;

import org.redis.config.RedisConfiguration;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        JedisPool jedisPool = RedisConfiguration.getJedisPool();
        RedisMap redisMap = new RedisMap(jedisPool);


        redisMap.clear();
        System.out.println("База данных Redis очищена.");


        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        redisMap.put("key3", "value3");
        System.out.println("Добавлены ключи: key1, key2, key3");


        int size = redisMap.size();
        System.out.println("Количество ключей в Redis: " + size);


        System.out.println("Пуста ли база данных Redis? " + redisMap.isEmpty());


        String existingKey = "key1";
        String nonExistingKey = "key100";
        System.out.println("Существует ли ключ '" + existingKey + "'? " + redisMap.containsKey(existingKey));
        System.out.println("Существует ли ключ '" + nonExistingKey + "'? " + redisMap.containsKey(nonExistingKey));


        String existingValue = "value1";
        String nonExistingValue = "value100";
        System.out.println("Существует ли значение '" + existingValue + "'? " + redisMap.containsValue(existingValue));
        System.out.println("Существует ли значение '" + nonExistingValue + "'? " + redisMap.containsValue(nonExistingValue));


        System.out.println("Значение для ключа 'key1': " + redisMap.get("key1"));


        redisMap.put("key4", "value4");
        System.out.println("Добавлен ключ 'key4' с значением 'value4'");
        System.out.println("Значение для 'key4': " + redisMap.get("key4"));
        System.out.println("Удаление ключа 'key4': " + redisMap.remove("key4"));


        Map<String, String> mapToPut = new HashMap<>();
        mapToPut.put("key5", "value5");
        mapToPut.put("key6", "value6");
        redisMap.putAll(mapToPut);
        System.out.println("Добавлены ключи key5 и key6");


        Set<String> keys = redisMap.keySet();
        System.out.println("Ключи в базе данных: " + keys);


        Collection<String> values = redisMap.values();
        System.out.println("Значения в базе данных: " + values);


        Set<Map.Entry<String, String>> entries = redisMap.entrySet();
        System.out.println("Все записи в базе данных:");
        for (Map.Entry<String, String> entry : entries) {
            System.out.println("Ключ: " + entry.getKey() + ", Значение: " + entry.getValue());
        }


        redisMap.clear();
        System.out.println("База данных Redis очищена в конце тестов.");
    }
}