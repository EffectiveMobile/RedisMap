package org.redis;

import org.redis.impl.RedisMap;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        RedisMap redisMap = new RedisMap("localhost", 6379);

        redisMap.put("lastName", "Zubenko");
        redisMap.put("firstName", "Mihail");
        redisMap.put("middleName", "Petrovich");
        redisMap.put("nickName", "Mafia");
        redisMap.put("age", "33");
        redisMap.put("city", "Voronezh");

        System.out.println("Фамилия: " + redisMap.get("lastName"));
        System.out.println("Имя: " + redisMap.get("firstName"));
        System.out.println("Отчество: " + redisMap.get("middleName"));
        System.out.println("Кличка: " + redisMap.get("nickName"));
        System.out.println("Возраст: " + redisMap.get("age"));
        System.out.println("Город: " + redisMap.get("city"));

        System.out.println("\nСодержит-ли ключ firstName: " + redisMap.containsKey("firstName"));
        System.out.println("Содержит-ли ключ country: " + redisMap.containsKey("country"));

        System.out.println("\nУдаляем ключ age...");
        System.out.println("Удаленное значение: " + redisMap.remove("age"));
        System.out.println("Пробуем получить age после удаления: " + redisMap.get("age"));

        System.out.println("\nВсе пары ключ-значение в RedisMap:");
        for (Map.Entry<String, String> entry : redisMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nПустая ли RedisMap? " + redisMap.isEmpty());

        System.out.println("Чистим RedisMap...");
        redisMap.clear();

        System.out.println("Пустая ли RedisMap? " + redisMap.isEmpty());
        System.out.println("Все пары ключ-значение после очистки RedisMap:");
        for (Map.Entry<String, String> entry : redisMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}