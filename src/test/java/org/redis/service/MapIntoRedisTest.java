package org.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapIntoRedisTest {

    private MapIntoRedis mapIntoRedis = new MapIntoRedis();

    @Test
    void size() {

        mapIntoRedis.put("neo", "max");

        assertEquals(1, mapIntoRedis.size(), "values: 1");
    }

    @Test
    void isEmpty() {

        mapIntoRedis.put("neo", "max");
        mapIntoRedis.remove("neo");

        assertTrue(mapIntoRedis.isEmpty(), "value: true");
    }

    @Test
    void containsKey() {

        mapIntoRedis.put("neo", "max");

        assertTrue(mapIntoRedis.containsKey("neo"), "value: true");
    }

    @Test
    void containsValue() {

        mapIntoRedis.put("neo", "max");

        assertTrue(mapIntoRedis.containsValue("max"), "value: true");
    }

    @Test
    void get() {
    }

    @Test
    void put() {
    }

    @Test
    void remove() {
        mapIntoRedis.put("neo", "max");
        mapIntoRedis.remove("neo");

        assertFalse(mapIntoRedis.containsKey("neo"), "value: false");
    }

    @Test
    void putAll() {

        Map<String, String> map = new HashMap<>();

        map.put("neo", "max");
        map.put("single", "min");

        mapIntoRedis.putAll(map);

        assertEquals(2, mapIntoRedis.size());
        assertEquals("min", mapIntoRedis.get("single"));
    }

    @Test
    void clear() {

        mapIntoRedis.put("neo", "max");
        mapIntoRedis.clear();

        assertTrue(mapIntoRedis.isEmpty(), "value: true");
    }
}