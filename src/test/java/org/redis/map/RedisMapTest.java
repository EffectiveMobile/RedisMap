package org.redis.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redis.map.exception.RedisMapException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisMapTest {

    private JedisPool poolMock;
    private Jedis jedisMock;
    private RedisMap redisMap;

    @BeforeEach
    void setUp() {
        poolMock = mock(JedisPool.class);
        jedisMock = mock(Jedis.class);
        when(poolMock.getResource()).thenReturn(jedisMock);
        redisMap = new RedisMap(poolMock);
    }

    @Test
    void sizeTest() {
        when(jedisMock.dbSize()).thenReturn(5L);

        assertEquals(5, redisMap.size());

        verify(jedisMock).dbSize();
        verify(poolMock).getResource();
    }

    @Test
    void isEmptyTest() {
        when(jedisMock.dbSize()).thenReturn(0L);

        assertTrue(redisMap.isEmpty());

        verify(jedisMock).dbSize();
        verify(poolMock).getResource();
    }

    @Test
    void containsKeyTest() {
        when(jedisMock.exists("1")).thenReturn(true);
        when(jedisMock.exists("7")).thenReturn(false);

        assertTrue(redisMap.containsKey("1"));
        assertFalse(redisMap.containsKey("7"));

        verify(jedisMock).exists("1");
        verify(jedisMock).exists("7");
        verify(poolMock, times(2)).getResource();
    }

    @Test
    void containsNullKeyTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.containsKey(null));
        assertEquals("Key can not be null", ex.getMessage());
    }

    @Test
    void containsValueTest() {
        when(jedisMock.keys("*")).thenReturn(Set.of("1", "2", "3"));
        when(jedisMock.get("1")).thenReturn("one");
        when(jedisMock.get("2")).thenReturn("two");
        when(jedisMock.get("3")).thenReturn("three");

        assertTrue(redisMap.containsValue("one"));
        assertFalse(redisMap.containsValue("k"));

        verify(jedisMock, times(2)).keys("*");
        verify(jedisMock, times(2)).get("1");
        verify(jedisMock, times(2)).get("2");
        verify(jedisMock, times(2)).get("3");
        verify(poolMock, times(2)).getResource();
    }

    @Test
    void containsNullValueTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.containsValue(null));
        assertEquals("Value can not be null", ex.getMessage());
    }

    @Test
    void getTest() {
        when(jedisMock.get("1")).thenReturn("one");

        assertEquals("one", redisMap.get("1"));

        verify(jedisMock).get("1");
        verify(poolMock).getResource();
    }

    @Test
    void getNullTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.get(null));
        assertEquals("Key can not be null", ex.getMessage());
    }

    @Test
    void putTest() {
        when(jedisMock.set("5", "five")).thenReturn("OK");
        when(jedisMock.get("5")).thenReturn("five");

        assertEquals("OK", redisMap.put("5", "five"));
        assertEquals("five", redisMap.get("5"));

        verify(jedisMock).set("5", "five");
        verify(jedisMock).get("5");
        verify(poolMock, times(2)).getResource();
    }

    @Test
    void putNullKeyTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.put(null, "one"));
        assertEquals("Key and value can not be null", ex.getMessage());
    }

    @Test
    void putNullValueTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.put("1", null));
        assertEquals("Key and value can not be null", ex.getMessage());
    }

    @Test
    void putNullKeyAndNullValueTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.put(null, null));
        assertEquals("Key and value can not be null", ex.getMessage());
    }

    @Test
    void removeTest() {
        when(jedisMock.del("1")).thenReturn(1L);
        when(jedisMock.del("-100")).thenReturn(0L);

        assertEquals("1", redisMap.remove("1"));
        assertEquals("0", redisMap.remove("-100"));

        verify(jedisMock).del("1");
        verify(jedisMock).del("-100");
        verify(poolMock, times(2)).getResource();
    }

    @Test
    void removeNullKeyTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.remove(null));
        assertEquals("Key can not be null", ex.getMessage());
    }

    @Test
    void putAllTest() {
        Map<String, String> map = new HashMap<>();
        map.put("4", "four");
        map.put("5", "five");
        map.put("10", "ten");

        redisMap.putAll(map);

        verify(jedisMock).mset(
          "4", "four", "5", "five", "10", "ten"
        );
        verify(poolMock).getResource();
    }

    @Test
    void putAllNullTest() {
        RedisMapException ex = assertThrows(RedisMapException.class, () -> redisMap.putAll(null));
        assertEquals("Map can not be null", ex.getMessage());
    }

    @Test
    void clearTest() {
        redisMap.clear();

        verify(jedisMock).flushDB();
        verify(poolMock).getResource();
    }

    @Test
    void keySetTest() {
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        when(jedisMock.keys("*")).thenReturn(set);

        Set<String> keys = redisMap.keySet();

        assertEquals(set.size(), keys.size());
        verify(jedisMock).keys("*");
        verify(poolMock).getResource();
    }

    @Test
    void valuesTest() {
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        List<String> list = List.of("one", "two", "three");
        when(jedisMock.keys("*")).thenReturn(set);
        when(jedisMock.mget(new String[] {"1", "2", "3"})).thenReturn(list);

        Collection<String> values = redisMap.values();

        assertEquals(list.size(), values.size());

        verify(jedisMock).keys("*");
        verify(jedisMock).mget(new String[] {"1", "2", "3"});
        verify(poolMock).getResource();
    }

    @Test
    void entrySetTest() {
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        when(jedisMock.keys("*")).thenReturn(set);

        Set<Map.Entry<String, String>> entries = redisMap.entrySet();

        assertEquals(set.size(), entries.size());
        verify(jedisMock).keys("*");
        verify(poolMock).getResource();
    }
}