package org.redis.service.Impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redis.exception.RedisMapOperationException;
import org.redis.repository.RedisMapRepository;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisMapServiceImplTest {

    @Mock
    private RedisMapRepository redisMapRepository;

    @InjectMocks
    private RedisMapServiceImpl redisMapService;

    @Test
    public void testSize() {
        when(redisMapRepository.size()).thenReturn(2);

        int size = redisMapService.size();

        assertEquals(2, size);
        verify(redisMapRepository, times(1)).size();
    }

    @Test
    public void testIsEmpty() {
        when(redisMapRepository.isEmpty()).thenReturn(true);

        boolean isEmpty = redisMapService.isEmpty();

        assertTrue(isEmpty);
        verify(redisMapRepository, times(1)).isEmpty();
    }

    @Test
    public void testContainsKey() {
        String key = "testKey";
        when(redisMapRepository.containsKey(key)).thenReturn(true);

        boolean containsKey = redisMapService.containsKey(key);

        assertTrue(containsKey);
        verify(redisMapRepository, times(1)).containsKey(key);
    }

    @Test
    public void testContainsValue() {
        String value = "testValue";
        when(redisMapRepository.containsValue(value)).thenReturn(true);

        boolean containsValue = redisMapService.containsValue(value);

        assertTrue(containsValue);
        verify(redisMapRepository, times(1)).containsValue(value);
    }

    @Test
    public void testGet() {
        String key = "testKey";
        String expectedValue = "testValue";
        when(redisMapRepository.get(key)).thenReturn(expectedValue);

        String value = redisMapService.get(key);

        assertEquals(expectedValue, value);
        verify(redisMapRepository, times(1)).get(key);
    }

    @Test
    public void testPut() {
        String key = "testKey";
        String value = "testValue";
        String oldValue = "oldValue";
        when(redisMapRepository.put(key, value)).thenReturn(oldValue);

        String result = redisMapService.put(key, value);

        assertEquals(oldValue, result);
        verify(redisMapRepository, times(1)).put(key, value);
    }

    @Test
    public void testRemove() {
        String key = "testKey";
        String expectedValue = "testValue";
        when(redisMapRepository.remove(key)).thenReturn(expectedValue);

        String value = redisMapService.remove(key);

        assertEquals(expectedValue, value);
        verify(redisMapRepository, times(1)).remove(key);
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        redisMapService.putAll(map);

        verify(redisMapRepository, times(1)).putAll(map);
    }

    @Test
    public void testClear() {
        redisMapService.clear();

        verify(redisMapRepository, times(1)).clear();
    }

    @Test
    public void testKeySet() {
        Set<String> expectedKeys = new HashSet<>(Arrays.asList("key1", "key2"));
        when(redisMapRepository.keySet()).thenReturn(expectedKeys);

        Set<String> keys = redisMapService.keySet();

        assertEquals(expectedKeys, keys);
        verify(redisMapRepository, times(1)).keySet();
    }

    @Test
    public void testValues() {
        Collection<String> expectedValues = Arrays.asList("value1", "value2");
        when(redisMapRepository.values()).thenReturn(expectedValues);

        Collection<String> values = redisMapService.values();

        assertEquals(expectedValues, values);
        verify(redisMapRepository, times(1)).values();
    }

    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, String>> expectedEntries = new HashSet<>();
        expectedEntries.add(new AbstractMap.SimpleEntry<>("key1", "value1"));
        expectedEntries.add(new AbstractMap.SimpleEntry<>("key2", "value2"));
        when(redisMapRepository.entrySet()).thenReturn(expectedEntries);

        Set<Map.Entry<String, String>> entries = redisMapService.entrySet();

        assertEquals(expectedEntries, entries);
        verify(redisMapRepository, times(1)).entrySet();
    }

    @Test
    public void testSizeThrowsException() {
        when(redisMapRepository.size()).thenThrow(
                new RedisMapOperationException("Error fetching size", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.size());

        assertEquals("Error fetching size", exception.getMessage());
        verify(redisMapRepository, times(1)).size();
    }

    @Test
    public void testIsEmptyThrowsException() {
        when(redisMapRepository.isEmpty()).thenThrow(
                new RedisMapOperationException("Error checking if empty", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.isEmpty());

        assertEquals("Error checking if empty", exception.getMessage());
        verify(redisMapRepository, times(1)).isEmpty();
    }

    @Test
    public void testContainsKeyThrowsException() {
        String key = "testKey";
        when(redisMapRepository.containsKey(key)).thenThrow(
                new RedisMapOperationException("Error checking key existence", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.containsKey(key));

        assertEquals("Error checking key existence", exception.getMessage());
        verify(redisMapRepository, times(1)).containsKey(key);
    }

    @Test
    public void testContainsValueThrowsException() {
        String value = "testValue";
        when(redisMapRepository.containsValue(value)).thenThrow(
                new RedisMapOperationException("Error checking value existence", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.containsValue(value));

        assertEquals("Error checking value existence", exception.getMessage());
        verify(redisMapRepository, times(1)).containsValue(value);
    }

    @Test
    public void testGetThrowsException() {
        String key = "testKey";
        when(redisMapRepository.get(key)).thenThrow(
                new RedisMapOperationException("Error fetching value", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.get(key));

        assertEquals("Error fetching value", exception.getMessage());
        verify(redisMapRepository, times(1)).get(key);
    }

    @Test
    public void testPutThrowsException() {
        String key = "testKey";
        String value = "testValue";
        when(redisMapRepository.put(key, value)).thenThrow(
                new RedisMapOperationException("Error putting value", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.put(key, value));

        assertEquals("Error putting value", exception.getMessage());
        verify(redisMapRepository, times(1)).put(key, value);
    }

    @Test
    public void testRemoveThrowsException() {
        String key = "testKey";
        when(redisMapRepository.remove(key)).thenThrow(
                new RedisMapOperationException("Error removing value", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.remove(key));

        assertEquals("Error removing value", exception.getMessage());
        verify(redisMapRepository, times(1)).remove(key);
    }

    @Test
    public void testPutAllThrowsException() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        doThrow(new RedisMapOperationException("Error putting all values", new Throwable("Root cause"))).when(
                        redisMapRepository)
                .putAll(map);

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.putAll(map));

        assertEquals("Error putting all values", exception.getMessage());
        verify(redisMapRepository, times(1)).putAll(map);
    }

    @Test
    public void testClearThrowsException() {
        doThrow(new RedisMapOperationException("Error clearing map", new Throwable("Root cause"))).when(
                        redisMapRepository)
                .clear();

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.clear());

        assertEquals("Error clearing map", exception.getMessage());
        verify(redisMapRepository, times(1)).clear();
    }

    @Test
    public void testKeySetThrowsException() {
        when(redisMapRepository.keySet()).thenThrow(
                new RedisMapOperationException("Error fetching keys", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.keySet());

        assertEquals("Error fetching keys", exception.getMessage());
        verify(redisMapRepository, times(1)).keySet();
    }

    @Test
    public void testValuesThrowsException() {
        when(redisMapRepository.values()).thenThrow(
                new RedisMapOperationException("Error fetching values", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.values());

        assertEquals("Error fetching values", exception.getMessage());
        verify(redisMapRepository, times(1)).values();
    }

    @Test
    public void testEntrySetThrowsException() {
        when(redisMapRepository.entrySet()).thenThrow(
                new RedisMapOperationException("Error fetching entries", new Throwable("Root cause")));

        RedisMapOperationException exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.entrySet());

        assertEquals("Error fetching entries", exception.getMessage());
        verify(redisMapRepository, times(1)).entrySet();
    }
}