package org.redis.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redis.exception.handler.RedisMapErrorHandler;
import org.redis.util.RedisMapConstantUtil;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class RedisMapRepositoryTest {

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>("redis:6.2.6")
            .withExposedPorts(6379);

    private JedisPool jedisPool;
    private RedisMapRepository redisMapRepository;

    @BeforeEach
    public void setup() {
        String address = RedisMapConstantUtil.REDIS_HOST;
        Integer port = RedisMapConstantUtil.REDIS_TEST_PORT;

        System.setProperty("REDIS_HOST", address);
        System.setProperty("REDIS_PORT", String.valueOf(port));

        jedisPool = new JedisPool(address, port);
        var errorHandler = new RedisMapErrorHandler();
        redisMapRepository = new RedisMapRepository(errorHandler);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @AfterEach
    public void teardown() {
        jedisPool.close();
    }

    @Test
    public void testSize() {
        redisMapRepository.put("key1", "value1");
        redisMapRepository.put("key2", "value2");
        assertEquals(2, redisMapRepository.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(redisMapRepository.isEmpty());
        redisMapRepository.put("key1", "value1");
        assertFalse(redisMapRepository.isEmpty());
    }

    @Test
    public void testContainsKey() {
        redisMapRepository.put("key1", "value1");
        assertTrue(redisMapRepository.containsKey("key1"));
        assertFalse(redisMapRepository.containsKey("key2"));
    }

    @Test
    public void testContainsValue() {
        redisMapRepository.put("key1", "value1");
        assertTrue(redisMapRepository.containsValue("value1"));
        assertFalse(redisMapRepository.containsValue("value2"));
    }

    @Test
    public void testGet() {
        redisMapRepository.put("key1", "value1");
        assertEquals("value1", redisMapRepository.get("key1"));
        assertNull(redisMapRepository.get("key2"));
    }

    @Test
    public void testPut() {
        assertNull(redisMapRepository.put("key1", "value1"));
        assertEquals("value1", redisMapRepository.get("key1"));
        assertEquals("value1", redisMapRepository.put("key1", "newValue"));
        assertEquals("newValue", redisMapRepository.get("key1"));
    }

    @Test
    public void testRemove() {
        redisMapRepository.put("key1", "value1");
        assertEquals("value1", redisMapRepository.remove("key1"));
        assertNull(redisMapRepository.remove("key2"));
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        redisMapRepository.putAll(map);
        assertEquals("value1", redisMapRepository.get("key1"));
        assertEquals("value2", redisMapRepository.get("key2"));
    }

    @Test
    public void testClear() {
        redisMapRepository.put("key1", "value1");
        redisMapRepository.put("key2", "value2");
        redisMapRepository.clear();
        assertTrue(redisMapRepository.isEmpty());
    }

    @Test
    public void testKeySet() {
        redisMapRepository.put("key1", "value1");
        redisMapRepository.put("key2", "value2");
        Set<String> keys = redisMapRepository.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }

    @Test
    public void testValues() {
        redisMapRepository.put("key1", "value1");
        redisMapRepository.put("key2", "value2");
        Collection<String> values = redisMapRepository.values();
        assertEquals(2, values.size());
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
    }

    @Test
    public void testEntrySet() {
        redisMapRepository.put("key1", "value1");
        redisMapRepository.put("key2", "value2");
        Set<Map.Entry<String, String>> entries = redisMapRepository.entrySet();
        assertEquals(2, entries.size());
        assertTrue(entries.contains(new AbstractMap.SimpleEntry<>("key1", "value1")));
        assertTrue(entries.contains(new AbstractMap.SimpleEntry<>("key2", "value2")));
    }
}