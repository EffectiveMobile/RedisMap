package org.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RedisMapTest {

    private static GenericContainer<?> redisContainer;
    private static JedisPool jedisPool;
    private RedisMap redisMap;

    @BeforeAll
    public static void setUpBeforeClass() {

        redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379);
        redisContainer.start();


        String redisHost = redisContainer.getHost();
        Integer redisPort = redisContainer.getMappedPort(6379);
        jedisPool = new JedisPool(redisHost, redisPort);
    }

    @BeforeEach
    public void setUp() {
        redisMap = new RedisMap(jedisPool);


        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @AfterEach
    public void tearDown() {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @AfterAll
    public static void tearDownAfterClass() {

        redisContainer.stop();
    }

    @Test
    public void testSize() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            jedis.set("key3", "value3");

            assertEquals(3, redisMap.size());
        }
    }

    @Test
    public void testIsEmpty() {
        assertTrue(redisMap.isEmpty());

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
        }

        assertFalse(redisMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertTrue(redisMap.containsKey("key1"));
            assertFalse(redisMap.containsKey("key3"));
        }
    }

    @Test
    public void testContainsValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertTrue(redisMap.containsValue("value1"));
            assertFalse(redisMap.containsValue("value3"));
        }
    }

    @Test
    public void testGet() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertEquals("value1", redisMap.get("key1"));
            assertNull(redisMap.get("key2"));
        }
    }

    @Test
    public void testPut() {
        redisMap.put("key1", "value1");
        assertEquals("value1", redisMap.get("key1"));

        redisMap.put("key2", "value2");
        assertEquals("value2", redisMap.get("key2"));
    }

    @Test
    public void testRemove() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertEquals("value1", redisMap.remove("key1"));
            assertNull(redisMap.get("key1"));
        }
    }

    @Test
    public void testPutAll() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        redisMap.putAll(data);

        assertEquals("value1", redisMap.get("key1"));
        assertEquals("value2", redisMap.get("key2"));
    }

    @Test
    public void testClear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            assertNotNull(redisMap.get("key1"));

            redisMap.clear();
            assertNull(redisMap.get("key1"));
        }
    }

    @Test
    public void testKeySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Set<String> keys = redisMap.keySet();
            assertTrue(keys.contains("key1"));
            assertTrue(keys.contains("key2"));
        }
    }

    @Test
    public void testValues() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Collection<String> values = redisMap.values();
            assertTrue(values.contains("value1"));
            assertTrue(values.contains("value2"));
        }
    }

    @Test
    public void testEntrySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Set<Map.Entry<String, String>> entries = redisMap.entrySet();
            assertTrue(entries.stream().anyMatch(entry -> entry.getKey().equals("key1") && entry.getValue().equals("value1")));
            assertTrue(entries.stream().anyMatch(entry -> entry.getKey().equals("key2") && entry.getValue().equals("value2")));
        }
    }
}
