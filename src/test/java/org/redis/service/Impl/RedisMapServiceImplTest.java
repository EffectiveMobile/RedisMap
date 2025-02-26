package org.redis.service.Impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redis.exception.RedisMapOperationException;
import org.redis.repository.RedisMapRepository;
import org.redis.validator.RedisMapKeyValidator;
import org.redis.validator.RedisMapValueValidator;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisMapServiceImplTest {

    @Mock
    private RedisMapRepository redisMapRepository;

    @InjectMocks
    private RedisMapServiceImpl redisMapService;

    private MockedStatic<RedisMapKeyValidator> keyValidatorMockedStatic;

    private MockedStatic<RedisMapValueValidator> valueValidatorMockedStatic;

    @BeforeEach
    public void setUp() {
        keyValidatorMockedStatic = mockStatic(RedisMapKeyValidator.class);
        valueValidatorMockedStatic = mockStatic(RedisMapValueValidator.class);
    }

    @AfterEach
    public void tearDown() {
        keyValidatorMockedStatic.close();
        valueValidatorMockedStatic.close();
    }

    @Test
    public void testSize() {
        when(redisMapRepository.size()).thenReturn(2);

        int size = redisMapService.size();

        assertEquals(2, size);
        verify(redisMapRepository, times(1)).size();

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
    }

    @Test
    public void testIsEmpty() {
        when(redisMapRepository.isEmpty()).thenReturn(true);

        boolean isEmpty = redisMapService.isEmpty();

        assertTrue(isEmpty);
        verify(redisMapRepository, times(1)).isEmpty();

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
    }

    @Test
    public void testContainsKey() {
        String key = "testKey";

        keyValidatorMockedStatic.when(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key))
                .thenAnswer(invocation -> null);

        when(redisMapRepository.containsKey(key)).thenReturn(true);

        boolean containsKey = redisMapService.containsKey(key);

        assertTrue(containsKey);
        verify(redisMapRepository, times(1)).containsKey(key);
        keyValidatorMockedStatic.verify(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key), times(1));
    }

    @Test
    public void testContainsValue() {
        String value = "testValue";

        valueValidatorMockedStatic.when(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value))
                .thenAnswer(invocation -> null);

        when(redisMapRepository.containsValue(value)).thenReturn(true);

        boolean containsValue = redisMapService.containsValue(value);

        assertTrue(containsValue);
        verify(redisMapRepository, times(1)).containsValue(value);
        valueValidatorMockedStatic.verify(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value), times(1));
    }

    @Test
    public void testGet() {
        String key = "testKey";
        String expectedValue = "testValue";

        keyValidatorMockedStatic.when(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key))
                .thenAnswer(invocation -> null);

        when(redisMapRepository.get(key)).thenReturn(expectedValue);

        String value = redisMapService.get(key);

        assertEquals(expectedValue, value);
        verify(redisMapRepository, times(1)).get(key);
        keyValidatorMockedStatic.verify(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key), times(1));
    }

    @Test
    public void testPut() {
        String key = "testKey";
        String value = "testValue";
        String oldValue = "oldValue";

        keyValidatorMockedStatic.when(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key))
                .thenAnswer(invocation -> null);
        valueValidatorMockedStatic.when(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value))
                .thenAnswer(invocation -> null);

        when(redisMapRepository.put(key, value)).thenReturn(oldValue);

        String result = redisMapService.put(key, value);

        assertEquals(oldValue, result);
        verify(redisMapRepository, times(1)).put(key, value);
        keyValidatorMockedStatic.verify(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key), times(1));
        valueValidatorMockedStatic.verify(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value), times(1));
    }

    @Test
    public void testRemove() {
        String key = "testKey";
        String expectedValue = "testValue";

        keyValidatorMockedStatic.when(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key))
                .thenAnswer(invocation -> null);

        when(redisMapRepository.remove(key)).thenReturn(expectedValue);

        String value = redisMapService.remove(key);

        assertEquals(expectedValue, value);
        verify(redisMapRepository, times(1)).remove(key);
        keyValidatorMockedStatic.verify(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key), times(1));
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        map.forEach((key, value) -> {
            keyValidatorMockedStatic.when(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key))
                    .thenAnswer(invocation -> null);
            valueValidatorMockedStatic.when(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value))
                    .thenAnswer(invocation -> null);
        });

        doNothing().when(redisMapRepository).putAll(map);

        redisMapService.putAll(map);

        verify(redisMapRepository, times(1)).putAll(map);
        map.forEach((key, value) -> {
            keyValidatorMockedStatic.verify(() -> RedisMapKeyValidator.validateKeyNotNullOrEmpty(key), times(1));
            valueValidatorMockedStatic.verify(() -> RedisMapValueValidator.validateValueNotNullOrEmpty(value),
                    times(1));
        });
    }

    @Test
    public void testClear() {
        doNothing().when(redisMapRepository).clear();

        redisMapService.clear();

        verify(redisMapRepository, times(1)).clear();

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
    }

    @Test
    public void testKeySet() {
        Set<String> expectedKeys = new HashSet<>(Arrays.asList("key1", "key2"));

        when(redisMapRepository.keySet()).thenReturn(expectedKeys);

        Set<String> keys = redisMapService.keySet();

        assertEquals(expectedKeys, keys);
        verify(redisMapRepository, times(1)).keySet();

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
    }

    @Test
    public void testValues() {
        Collection<String> expectedValues = Arrays.asList("value1", "value2");

        when(redisMapRepository.values()).thenReturn(expectedValues);

        Collection<String> values = redisMapService.values();

        assertEquals(expectedValues, values);
        verify(redisMapRepository, times(1)).values();

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
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

        keyValidatorMockedStatic.verifyNoInteractions();
        valueValidatorMockedStatic.verifyNoInteractions();
    }

    @Test
    public void testSizeThrowsException() {
        when(redisMapRepository.size()).thenThrow(
                new RedisMapOperationException("Error fetching size", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.size());

        assertEquals("Error fetching size", exception.getMessage());
        verify(redisMapRepository, times(1)).size();
    }

    @Test
    public void testIsEmptyThrowsException() {
        when(redisMapRepository.isEmpty()).thenThrow(
                new RedisMapOperationException("Error checking if empty", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.isEmpty());

        assertEquals("Error checking if empty", exception.getMessage());
        verify(redisMapRepository, times(1)).isEmpty();
    }

    @Test
    public void testContainsKeyThrowsException() {
        String key = "testKey";

        when(redisMapRepository.containsKey(key)).thenThrow(
                new RedisMapOperationException("Error checking key existence", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.containsKey(key));

        assertEquals("Error checking key existence", exception.getMessage());
        verify(redisMapRepository, times(1)).containsKey(key);
    }

    @Test
    public void testContainsValueThrowsException() {
        String value = "testValue";

        when(redisMapRepository.containsValue(value)).thenThrow(
                new RedisMapOperationException("Error checking value existence", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.containsValue(value));

        assertEquals("Error checking value existence", exception.getMessage());
        verify(redisMapRepository, times(1)).containsValue(value);
    }

    @Test
    public void testGetThrowsException() {
        String key = "testKey";

        when(redisMapRepository.get(key)).thenThrow(
                new RedisMapOperationException("Error fetching value", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
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

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.put(key, value));

        assertEquals("Error putting value", exception.getMessage());
        verify(redisMapRepository, times(1)).put(key, value);
    }

    @Test
    public void testRemoveThrowsException() {
        String key = "testKey";

        when(redisMapRepository.remove(key)).thenThrow(
                new RedisMapOperationException("Error removing value", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
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

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.putAll(map));

        assertEquals("Error putting all values", exception.getMessage());
        verify(redisMapRepository, times(1)).putAll(map);
    }

    @Test
    public void testClearThrowsException() {
        doThrow(new RedisMapOperationException("Error clearing map", new Throwable("Root cause"))).when(
                        redisMapRepository)
                .clear();

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.clear());

        assertEquals("Error clearing map", exception.getMessage());
        verify(redisMapRepository, times(1)).clear();
    }

    @Test
    public void testKeySetThrowsException() {
        when(redisMapRepository.keySet()).thenThrow(
                new RedisMapOperationException("Error fetching keys", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.keySet());

        assertEquals("Error fetching keys", exception.getMessage());
        verify(redisMapRepository, times(1)).keySet();
    }

    @Test
    public void testValuesThrowsException() {
        when(redisMapRepository.values()).thenThrow(
                new RedisMapOperationException("Error fetching values", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.values());

        assertEquals("Error fetching values", exception.getMessage());
        verify(redisMapRepository, times(1)).values();
    }

    @Test
    public void testEntrySetThrowsException() {
        when(redisMapRepository.entrySet()).thenThrow(
                new RedisMapOperationException("Error fetching entries", new Throwable("Root cause")));

        var exception = assertThrows(RedisMapOperationException.class,
                () -> redisMapService.entrySet());

        assertEquals("Error fetching entries", exception.getMessage());
        verify(redisMapRepository, times(1)).entrySet();
    }
}