package org.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class RedisMapTest {

    private RedisMap redisMap;

    @BeforeEach
    void setUp() {
        JedisPool jedisPool = new JedisPool("localhost", 6379);
        redisMap = new RedisMap(jedisPool, 1);
        redisMap.clear();
    }

    @DisplayName("Проверка size(), если Redis не пуст")
    @Test
    void sizeTest_ifRedisNotEmpty() {
        redisMap.put("key", "value");
        int result = redisMap.size();
        int expected = 1;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка size(), если Redis пуст")
    @Test
    void sizeTest_ifRedisEmpty() {
        int result = redisMap.size();
        int expected = 0;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка isEmpty(), если Redis пуст")
    @Test
    void isEmptyTest_ifRedisEmpty() {
        boolean result = redisMap.isEmpty();
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка isEmpty(), если Redis пуст")
    @Test
    void isEmptyTest_ifRedisNotEmpty() {
        boolean result = redisMap.isEmpty();
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка containsKey(), если ключ есть")
    @Test
    void containsKeyTest_ifKeyExists() {
        redisMap.put("key", "value");
        boolean result = redisMap.containsKey("key");
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка containsKey(), если ключ есть")
    @Test
    void containsKeyTest_ifKeyNotExists() {
        redisMap.put("key", "value");
        boolean result = redisMap.containsKey("key");
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка containsKey(), если ключа нет")
    @Test
    void containsValueTest_ifKeyExists() {
        redisMap.put("key", "value");
        boolean result = redisMap.containsValue("value");
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка containsKey(), если ключа нет")
    @Test
    void containsValueTest_ifKeyNotExists() {
        boolean result = redisMap.containsValue("value");
        boolean expected = false;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка get(), если ключ и значение по нему есть")
    @Test
    void getTest_ifKeyValueExists() {
        redisMap.put("key", "value");
        String result = redisMap.get("key");
        String expected = "value";
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка get(), если ключа и значения по нему нет")
    @Test
    void getTest_ifKeyValueNotExists() {
        String result = redisMap.get("key");
        Assertions.assertNull(result);
    }

    @DisplayName("Проверка put()")
    @Test
    void putTest() {
        String result = redisMap.put("key", "value");
        String expected = "OK";
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка remove(), если ключ есть")
    @Test
    void removeTest_ifKeyForRemoveExists() {
        redisMap.put("key", "value");
        String result = redisMap.remove("key");
        String expected = "1";
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка remove(), если ключа нет")
    @Test
    void removeTest_ifKeyForRemoveNotExists() {
        String result = redisMap.remove("key");
        String expected = "0";
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка putAll()")
    @Test
    void putAllTest() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("key1", "value1");
        redisMap.putAll(map);
        String result = redisMap.get("key");
        String expected = "value";
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка clear()")
    @Test
    void clearTest() {
        redisMap.put("key", "value");
        redisMap.clear();
        int resultSize = redisMap.size();
        int expectedSize = 0;
        Assertions.assertEquals(expectedSize, resultSize);
        String resultGet = redisMap.get("key");
        Assertions.assertNull(resultGet);
    }

    @DisplayName("Проверка keySet(), если ключи есть")
    @Test
    void keySetTest_ifKeyExists() {
        redisMap.put("key", "value");
        redisMap.put("key2", "value");
        redisMap.put("key3", "value");
        redisMap.put("key4", "value");
        Set<String> set = redisMap.keySet();
        int resultSize = set.size();
        int expectedSize = 4;
        Assertions.assertEquals(expectedSize, resultSize);

        for (String key : set) {
            Assertions.assertNotNull(key);
        }
    }

    @DisplayName("Проверка keySet(), если ключей нет")
    @Test
    void keySetTest_ifKeyNotExists() {
        Set<String> set = redisMap.keySet();
        boolean result = set.isEmpty();
        boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @DisplayName("Проверка values(), если значения есть")
    @Test
    void valuesTest_ifValuesExists() {
        redisMap.put("key", "value");
        redisMap.put("key2", "value");
        redisMap.put("key3", "value");
        redisMap.put("key4", "value");
        Collection<String> set = redisMap.values();
        int resultSize = set.size();
        int expectedSize = 4;
        Assertions.assertEquals(expectedSize, resultSize);

        boolean result;
        for (String values : set) {
            result = redisMap.containsValue(values);
            Assertions.assertTrue(result);
        }
    }

    @DisplayName("Проверка values(), если значений нет")
    @Test
    void valuesTest_ifValuesNotExists() {
        Collection<String> set = redisMap.values();
        boolean result = set.isEmpty();
        Assertions.assertTrue(result);
    }

    @DisplayName("Проверка entrySet()")
    @Test
    void entrySetTest() {
        String key1 = "key";
        String value1 = "value";
        String key2 = "key2";
        String value2 = "value2";
        redisMap.put(key1, value1);
        redisMap.put(key2, value2);

        Set<Map.Entry<String, String>> set = redisMap.entrySet();

        boolean result = set.isEmpty();
        boolean expected = false;
        Assertions.assertEquals(expected, result);

        int resultSize = set.size();
        int expectedSize = 2;
        Assertions.assertEquals(expectedSize, resultSize);

        String resultKey;
        String resultValue;
        boolean resultKeyExists;
        boolean resultValueExists;
        for (Map.Entry<String, String> entry : set) {
            resultKey = entry.getKey();
            resultValue = entry.getValue();
            resultKeyExists = redisMap.containsKey(resultKey);
            resultValueExists = redisMap.containsValue(resultValue);
            Assertions.assertTrue(resultKeyExists);
            Assertions.assertTrue(resultValueExists);
        }
    }

}
