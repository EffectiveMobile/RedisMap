package org.redis.service;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MapIntoRedisTest {

    private MapIntoRedis mapIntoRedis = new MapIntoRedis();


    @Test
    void size() {


        mapIntoRedis.clear();

        mapIntoRedis.put("neo", "max");

        assertEquals(1, mapIntoRedis.size(), "values: 1");
        mapIntoRedis.clear();
    }

    @Test
    void isEmpty() {

        mapIntoRedis.put("neo", "max");
        mapIntoRedis.remove("neo");

        assertTrue(mapIntoRedis.isEmpty(), "value: true");
        mapIntoRedis.clear();
    }

    @Test
    void containsKey() {

        mapIntoRedis.put("neo", "max");

        assertTrue(mapIntoRedis.containsKey("neo"), "value: true");
        mapIntoRedis.clear();
    }

    @Test
    void containsValue() {

        mapIntoRedis.put("neo", "max");

        assertTrue(mapIntoRedis.containsValue("max"), "value: true");
        mapIntoRedis.clear();
    }

    @Test
    void get() {

        mapIntoRedis.put("neo", "max");

        assertEquals("max", mapIntoRedis.get("neo"), "value: max");

        mapIntoRedis.clear();
    }

    @Test
    void put() {

        mapIntoRedis.put("neo", "max");

        assertTrue(mapIntoRedis.containsKey("neo"), "value: neo");

        mapIntoRedis.clear();
    }

    @Test
    void remove() {
        mapIntoRedis.put("neo", "max");
        mapIntoRedis.remove("neo");

        assertFalse(mapIntoRedis.containsKey("neo"), "value: neo");
        mapIntoRedis.clear();
    }

    @Test
    void putAll() {

        Map<String, String> map = new HashMap<>();

        map.put("neo", "max");
        map.put("single", "min");

        mapIntoRedis.putAll(map);

        assertEquals(2, mapIntoRedis.size());
        assertEquals("min", mapIntoRedis.get("single"));
        mapIntoRedis.clear();
    }

    @Test
    void clear() {

        mapIntoRedis.put("neo", "max");
        mapIntoRedis.clear();

        assertTrue(mapIntoRedis.isEmpty(), "value: true");
        mapIntoRedis.clear();
    }
}