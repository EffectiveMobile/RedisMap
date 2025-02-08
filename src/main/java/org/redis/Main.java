package org.redis;

import org.redis.impl.RedisMap;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // Подключение к Redis
        try (JedisPool jedisPool = new JedisPool("localhost", 6379)) {

            // Создаем экземпляр RedisMap с префиксом "app" и "app2"
            Map<String, String> redisMap = new RedisMap(jedisPool, "app");
            Map<String, String> redisMap2 = new RedisMap(jedisPool, "app2");

            // Добавление значений
            redisMap.put("key1", "val1");
            redisMap.put("key2", "val2");
            redisMap.put("key3", "val3");
            redisMap.put("key4", "val4");

            redisMap2.put("key1", "val1");
            redisMap2.put("key2", "val2");


            // Получаем и выводим значения
            System.out.println("Key1: " + redisMap.get("key1"));
            System.out.println("Key3: " + redisMap.get("key3"));

            //все пары
            System.out.println(redisMap.entrySet());

            //все ключи
            System.out.println(redisMap.keySet());

            //все значения
            System.out.println(redisMap.values());

            // Проверяем наличие ключей
            System.out.println("Contains key 'key1': " + redisMap.containsKey("key1"));

            // Удаляем значение
            redisMap.remove("key1");

            // Проверяем после удаления
            System.out.println("Contains key 'key1' after removal: " + redisMap.containsKey("key1"));

            // Подготовим тестовые данные для putAll
            Map<String, String> testData = new HashMap<>();
            testData.put("test1", "value1");
            testData.put("test2", "value2");
            testData.put("test3", "value3");

            // Вызываем метод putAll
            redisMap.putAll(testData);

            //все пары
            System.out.println(redisMap.entrySet());

            // Очищаем RedisMap
            redisMap.clear();
            System.out.println("Redis with prefix app: " + redisMap.entrySet() + " Redis with prefix app2: " + redisMap2.entrySet());
        }
    }
}