package org.redis;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class RedisMapTest {
    private static GenericContainer<?> redisContainer;
    private JedisPool jedisPool;
    private RedisMap redisMap;

    @BeforeAll
    static void startContainer() {
        redisContainer = new GenericContainer<>("redis:latest")
                .withExposedPorts(6379);
        redisContainer.start();
    }

    @BeforeEach
    void setUp() {
        String address = redisContainer.getHost();
        Integer port = redisContainer.getMappedPort(6379);
        jedisPool = new JedisPool(address, port);
        redisMap = new RedisMap(jedisPool);
    }

    @AfterAll
    static void stopContainer() {
        redisContainer.stop();
    }

    private void withJedis(Consumer<Jedis> jedisConsumer) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedisConsumer.accept(jedis);
        }
    }

    @Test
    void testSize() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            assertEquals(2, redisMap.size());
        });
    }

    @Test
    void testIsEmpty() {
        withJedis(jedis -> {
            jedis.flushDB();
            assertTrue(redisMap.isEmpty());

            jedis.set("key1", "value1");
            assertFalse(redisMap.isEmpty());
        });
    }

    @Test
    void testContainsKey() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            assertTrue(redisMap.containsKey("key1"));
        });
    }

    @Test
    void testContainsValue() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertTrue(redisMap.containsValue("value1"));
            assertFalse(redisMap.containsValue("value3"));
        });
    }

    @Test
    void testGet() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            assertEquals("value1", redisMap.get("key1"));
        });
    }

    @Test
    void testPut() {
        withJedis(jedis -> {
            jedis.set("key1", "oldValue");
            assertEquals("oldValue", redisMap.put("key1", "newValue"));
            assertEquals("newValue", jedis.get("key1"));
        });
    }

    @Test
    void testRemove() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            assertEquals("value1", redisMap.remove("key1"));
            assertNull(jedis.get("key1"));
        });
    }

    @Test
    void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        redisMap.putAll(map);
        withJedis(jedis -> {
            assertEquals("value1", jedis.get("key1"));
            assertEquals("value2", jedis.get("key2"));
        });
    }

    @Test
    void testClear() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            redisMap.clear();
            assertEquals(0, jedis.dbSize());
        });
    }

    @Test
    void testKeySet() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            assertEquals(Set.of("key1", "key2"), redisMap.keySet());
        });
    }

    @Test
    void testValues() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            assertTrue(redisMap.values().containsAll(Set.of("value1", "value2")));
        });
    }

    @Test
    void testEntrySet() {
        withJedis(jedis -> {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            assertEquals(2, redisMap.entrySet().size());
        });
    }
}