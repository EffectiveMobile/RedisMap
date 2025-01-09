package org.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.redis.impl.RedisMap;

public class RedisMapTest {
    private final RedisMap redisMap = new RedisMap("localhost", 6379);

    @BeforeEach
    public void clearRedisMap(){
        redisMap.clear();
    }

    @Test
    public void testSize() {
        assertEquals(0, redisMap.size(), "Initial size should be 0");

        redisMap.put("key1", "value1");
        assertEquals(1, redisMap.size(), "Size should be 1 after adding one element");

        redisMap.put("key2", "value2");
        assertEquals(2, redisMap.size(), "Size should be 2 after adding two elements");

        redisMap.remove("key1");
        assertEquals(1, redisMap.size(), "Size should be 1 after removing one element");
    }

    @Test
    public void testIsEmpty() {
        assertTrue(redisMap.isEmpty(), "Map should be empty initially");

        redisMap.put("key1", "value1");
        assertFalse(redisMap.isEmpty(), "Map should not be empty after adding an element");

        redisMap.clear();
        assertTrue(redisMap.isEmpty(), "Map should be empty after clear");
    }

    @Test
    public void testContainsKey() {
        assertFalse(redisMap.containsKey("key1"), "Map should not contain key1 initially");

        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsKey("key1"), "Map should contain key1 after adding it");

        redisMap.remove("key1");
        assertFalse(redisMap.containsKey("key1"), "Map should not contain key1 after removal");
    }

    @Test
    public void testContainsValue() {
        assertFalse(redisMap.containsValue("value1"), "Map should not contain value1 initially");

        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsValue("value1"), "Map should contain value1 after adding it");

        redisMap.remove("key1");
        assertFalse(redisMap.containsValue("value1"), "Map should not contain value1 after removal");
    }

    @Test
    public void testGet() {
        assertNull(redisMap.get("key1"), "get() should return null for non-existent key");

        redisMap.put("key1", "value1");
        assertEquals("value1", redisMap.get("key1"), "get() should return the correct value for key1");

        redisMap.put("key1", "updatedValue");
        assertEquals("updatedValue", redisMap.get("key1"), "get() should return the updated value for key1");
    }

    @Test
    public void testPut() {
        assertNull(redisMap.put("key1", "value1"), "put() should return null for new key");

        assertEquals("value1", redisMap.put("key1", "updatedValue"), "put() should return the previous value for existing key");

        assertEquals("updatedValue", redisMap.get("key1"), "put() should update the value for existing key");
    }

    @Test
    public void testRemove() {
        assertNull(redisMap.remove("key1"), "remove() should return null for non-existent key");

        redisMap.put("key1", "value1");
        assertEquals("value1", redisMap.remove("key1"), "remove() should return the value for removed key");

        assertNull(redisMap.get("key1"), "get() should return null after removal");
    }

    @Test
    public void testPutAll() {
        Map<String, String> newEntries = Map.of("key1", "value1", "key2", "value2");

        redisMap.putAll(newEntries);

        assertEquals("value1", redisMap.get("key1"), "putAll() should add key1");
        assertEquals("value2", redisMap.get("key2"), "putAll() should add key2");
    }

    @Test
    public void testClear() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");

        redisMap.clear();

        assertEquals(0, redisMap.size(), "clear() should remove all entries");
        assertTrue(redisMap.isEmpty(), "clear() should make the map empty");
    }

    @Test
    public void testKeySet() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");

        Set<String> keys = redisMap.keySet();

        assertEquals(2, keys.size(), "keySet() should return 2 keys");
        assertTrue(keys.contains("key1"), "keySet() should contain key1");
        assertTrue(keys.contains("key2"), "keySet() should contain key2");
    }

    @Test
    public void testValues() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");

        Collection<String> values = redisMap.values();

        assertEquals(2, values.size(), "values() should return 2 values");
        assertTrue(values.contains("value1"), "values() should contain value1");
        assertTrue(values.contains("value2"), "values() should contain value2");
    }

    @Test
    public void testEntrySet() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");

        Set<Map.Entry<String, String>> entries = redisMap.entrySet();

        assertEquals(2, entries.size(), "entrySet() should return 2 entries");
        for (Map.Entry<String, String> entry : entries) {
            assertTrue(entry.getKey().equals("key1") || entry.getKey().equals("key2"), "entrySet() should contain correct keys");
            assertTrue(entry.getValue().equals("value1") || entry.getValue().equals("value2"), "entrySet() should contain correct values");
        }
    }
}
