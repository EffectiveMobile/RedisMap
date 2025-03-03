import org.junit.jupiter.api.*;
import org.redis.RedisMap;
import redis.clients.jedis.Jedis;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RedisMapTest {

    private static Jedis jedis;
    private RedisMap redisMap;

    @BeforeAll
    static void setUpAll() {
        jedis = new Jedis("localhost", 6379);
    }

    @AfterAll
    static void tearDownAll() {
        if (jedis != null) {
            jedis.close();
        }
    }

    @BeforeEach
    void setUp() {
        redisMap = new RedisMap("testMap", "localhost", 6379);
    }

    @AfterEach
    void tearDown() {
        if (redisMap != null) {
            jedis.flushDB();
            redisMap.close();
        }
    }

    @Test
    void testPutAndGet() {
        redisMap.put("key1", "value1");
        assertEquals("value1", redisMap.get("key1"));
    }

    @Test
    void testContainsKey() {
        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsKey("key1"));
        assertFalse(redisMap.containsKey("key2"));
    }

    @Test
    void testContainsValue() {
        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsValue("value1"));
        assertFalse(redisMap.containsValue("value2"));
    }

    @Test
    void testRemove() {
        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsKey("key1"));
        redisMap.remove("key1");
        assertFalse(redisMap.containsKey("key1"));
    }

    @Test
    void testSize() {
        assertEquals(0, redisMap.size());
        redisMap.put("key1", "value1");
        assertEquals(1, redisMap.size());
        redisMap.put("key2", "value2");
        assertEquals(2, redisMap.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(redisMap.isEmpty());
        redisMap.put("key1", "value1");
        assertFalse(redisMap.isEmpty());
    }

    @Test
    void testKeySet() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        Set<String> keys = redisMap.keySet();
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertEquals(2, keys.size());
    }

    @Test
    void testValues() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        Collection<String> values = redisMap.values();
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
        assertEquals(2, values.size());
    }

    @Test
    void testEntrySet() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        Set<Map.Entry<String, String>> entries = redisMap.entrySet();
        assertEquals(2, entries.size());
        for (Map.Entry<String, String> entry : entries) {
            assertTrue(entry.getKey().equals("key1") || entry.getKey().equals("key2"));
            assertTrue(entry.getValue().equals("value1") || entry.getValue().equals("value2"));
        }
    }

    @Test
    void testClear() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        redisMap.clear();
        assertEquals(0, redisMap.size());
    }
}