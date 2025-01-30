package org.redis.map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RedisMapTest Tests")
public class RedisMapTest {
    private static GenericContainer<?> redisContainer;
    private RedisMap redisMap;
    private static JedisPool jedisPool;

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

    @AfterAll
    public static void tearDownAfterClass() {
        redisContainer.stop();
    }

    @Test
    @DisplayName("size() test.")
    void givenDataWhenSizeThenCorrectSize() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            jedis.set("key3", "value3");

            assertEquals(3, redisMap.size());
        }
    }

    @Test
    @DisplayName("IsEmpty() test with not empty map.")
    void givenNotEmptyMapWhenIsEmptyThenFalse() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertFalse(redisMap.isEmpty());
        }
    }

    @Test
    @DisplayName("IsEmpty() test with empty map.")
    void givenEmptyMapWhenIsEmptyThenTrue() {
        assertTrue(redisMap.isEmpty());
    }

    @Test
    @DisplayName("ContainsKey() test: false with null argument.")
    void givenNullWhenContainsKeyThenFalse() {
        assertFalse(redisMap.containsKey(null));
    }

    @Test
    @DisplayName("ContainsKey() test: false with incorrect argument.")
    void givenIncorrectKeyWhenContainsKeyThenFalse() {
        assertFalse(redisMap.containsKey("1"));
    }

    @Test
    @DisplayName("ContainsKey() test with correct data.")
    void givenCorrectKeyWhenContainsKeyThenTrue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertTrue(redisMap.containsKey("key1"));
        }
    }

    @Test
    @DisplayName("ContainsValue() test: false with null argument.")
    void givenNullWhenContainsValueThenFalse() {
        assertFalse(redisMap.containsValue(null));
    }

    @Test
    @DisplayName("ContainsValue() test: false with incorrect argument.")
    void givenIncorrectValueWhenContainsValueThenFalse() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertFalse(redisMap.containsValue("1"));
            assertFalse(redisMap.containsValue("key1"));
        }
    }

    @Test
    @DisplayName("ContainsValue() test with correct data.")
    void givenCorrectValueWhenContainsValueThenTrue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertTrue(redisMap.containsValue("value1"));
            assertTrue(redisMap.containsValue("value2"));
        }
    }

    @Test
    @DisplayName("ContainsValue() test with incorrect data.")
    void givenIncorrectValueWhenContainsValueThenTrue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertFalse(redisMap.containsValue("value3"));
            assertFalse(redisMap.containsValue("key2"));
        }
    }

    @Test
    @DisplayName("get() test with correct data.")
    void givenCorrectDataWhenGetThenValue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertEquals("value1", redisMap.get("key1"));
        }
    }

    @Test
    @DisplayName("get() test with null argument.")
    void givenNullArgumentWhenGetThenThrow() {
        assertThrows(IllegalArgumentException.class, () -> redisMap.get(null));
    }

    @Test
    @DisplayName("put() test with correct data.")
    void putTest() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        try (Jedis jedis = jedisPool.getResource()) {
            assertEquals("value1", jedis.get("key1"));
            assertEquals("value2", jedis.get("key2"));
        }
    }

    @Test
    @DisplayName("put() test with null key.")
    void givenNullKeyWhenPutThenThrow() {
        assertThrows(IllegalArgumentException.class, () -> redisMap.put(null, "1"));
    }

    @Test
    @DisplayName("put() test with null value.")
    void givenNullValueWhenPutThenThrow() {
        assertThrows(IllegalArgumentException.class, () -> redisMap.put("1", null));
    }

    @Test
    @DisplayName("put() test with null data.")
    void givenNullArgumentsWhenPutThenThrow() {
        assertThrows(IllegalArgumentException.class, () -> redisMap.put(null, null));
    }

    @Test
    @DisplayName("remove() test with correct data.")
    void removeTest() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            assertEquals("value1", redisMap.remove("key1"));
            assertEquals("value2", redisMap.remove("key2"));
            assertEquals("null", redisMap.remove("-key2"));
            assertNull(jedis.get("key1"));
            assertNull(jedis.get("key2"));
        }
    }

    @Test
    @DisplayName("remove() test with null argument.")
    void givenNullArgumentWhenRemoveThenThrow() {
        assertThrows(IllegalArgumentException.class, () -> redisMap.remove(null));
    }

    @Test
    @DisplayName("putAll() test with correct data.")
    void givenCorrectMapWhenPutAllThenUpdateData() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        redisMap.putAll(data);

        try (Jedis jedis = jedisPool.getResource()) {
            assertEquals("value1", jedis.get("key1"));
            assertEquals("value2", jedis.get("key2"));
        }
    }

    @Test
    @DisplayName("putAll() test with null argument.")
    void givenNullArgumentWhenPutAllThenNothing() {
        try (Jedis jedis = jedisPool.getResource()) {
            redisMap.putAll(null);

            assertEquals(0, jedis.dbSize());
        }
    }

    @Test
    @DisplayName("putAll() test with empty map argument.")
    void givenEmptyMapWhenPutAllThenNothing() {
        try (Jedis jedis = jedisPool.getResource()) {
            redisMap.putAll(Map.of());

            assertEquals(0, jedis.dbSize());
        }
    }

    @Test
    @DisplayName("clear() test.")
    void givenDataWhenClearThenEmpty() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");

            assertNotNull(jedis.get("key1"));

            redisMap.clear();

            assertNull(jedis.get("key1"));
            assertEquals(0, jedis.dbSize());
        }
    }

    @Test
    @DisplayName("keySet() test.")
    void givenDataWhenKeySetThenSetResult() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Set<String> keys = redisMap.keySet();

            assertTrue(keys.contains("key1"));
            assertTrue(keys.contains("key2"));
            assertEquals(2, keys.size());
        }
    }

    @Test
    @DisplayName("valuesTest() test.")
    void givenDataWhenValuesThenSetResult() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Collection<String> values = redisMap.values();

            assertTrue(values.contains("value1"));
            assertTrue(values.contains("value2"));
            assertEquals(2, values.size());
        }
    }

    @Test
    @DisplayName("entrySetTest() test.")
    void givenDataWhenEntrySetThenSetResult() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");

            Set<Map.Entry<String, String>> entries = redisMap.entrySet();

            assertTrue(entries.stream()
                    .anyMatch(entry -> entry.getKey().equals("key1")
                            && entry.getValue().equals("value1"))
            );
            assertTrue(entries.stream()
                    .anyMatch(entry -> entry.getKey().equals("key2")
                            && entry.getValue().equals("value2"))
            );
        }
    }
}
