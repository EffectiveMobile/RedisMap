package org.redis.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redis.repository.RedisMapRepository;
import org.redis.service.RedisMapService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link RedisMapService} for managing key-value pairs in Redis.
 */
@Slf4j
@RequiredArgsConstructor
public class RedisMapServiceImpl implements RedisMapService {
    private final RedisMapRepository redisMapRepository;

    /**
     * Returns the number of entries in Redis.
     */
    @Override
    public int size() {
        log.info("Requested size of Redis map");

        int size = redisMapRepository.size();
        log.info("Redis map size: {}", size);

        return size;
    }

    /**
     * Checks if Redis is empty.
     */
    @Override
    public boolean isEmpty() {
        log.info("Checking if Redis map is empty");

        boolean isEmpty = redisMapRepository.isEmpty();
        log.info("Redis map is empty: {}", isEmpty);

        return isEmpty;
    }

    /**
     * Checks if the given key exists in Redis.
     */
    @Override
    public boolean containsKey(String key) {
        log.info("Checking if Redis map contains key: '{}'", key);

        boolean containsKey = redisMapRepository.containsKey(key);
        log.info("Redis map contains key '{}': {}", key, containsKey);

        return containsKey;
    }

    /**
     * Checks if the given value exists in Redis.
     */
    @Override
    public boolean containsValue(String value) {
        log.info("Checking if Redis map contains value: '{}'", value);

        boolean containsValue = redisMapRepository.containsValue(value);
        log.info("Redis map contains value '{}': {}", value, containsValue);

        return containsValue;
    }

    /**
     * Retrieves the value for the given key.
     */
    @Override
    public String get(String key) {
        log.info("Fetching value for key: '{}'", key);

        String value = redisMapRepository.get(key);
        log.info("Retrieved value for key '{}': '{}'", key, value);

        return value;
    }

    /**
     * Adds or updates a key-value pair in Redis.
     */
    @Override
    public String put(String key, String value) {
        log.info("Inserting value '{}' for key: '{}'", value, key);

        String oldValue = redisMapRepository.put(key, value);
        log.info("Old value for key '{}': '{}'", key, oldValue);

        return oldValue;
    }

    /**
     * Removes a key from Redis.
     */
    @Override
    public String remove(String key) {
        log.info("Removing key: '{}'", key);

        String removedValue = redisMapRepository.remove(key);
        log.info("Removed value for key '{}': '{}'", key, removedValue);

        return removedValue;
    }

    /**
     * Adds multiple key-value pairs to Redis.
     */
    @Override
    public void putAll(Map<String, String> map) {
        log.info("Inserting {} key-value pairs into Redis map: {}", map.size(), map);

        redisMapRepository.putAll(map);
        log.info("Successfully inserted key-value pairs into Redis map");
    }

    /**
     * Clears all entries in Redis.
     */
    @Override
    public void clear() {
        log.info("Clearing all data from Redis map");

        redisMapRepository.clear();
        log.info("Redis map cleared successfully");
    }

    /**
     * Returns all keys in Redis.
     */
    @Override
    public Set<String> keySet() {
        log.info("Fetching all keys from Redis map");

        Set<String> keys = redisMapRepository.keySet();
        log.info("Retrieved {} keys from Redis map", keys.size());

        return keys;
    }

    /**
     * Returns all values in Redis.
     */
    @Override
    public Collection<String> values() {
        log.info("Fetching all values from Redis map");

        Collection<String> values = redisMapRepository.values();
        log.info("Retrieved {} values from Redis map", values.size());

        return values;
    }

    /**
     * Returns all key-value entries in Redis.
     */
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        log.info("Fetching all key-value pairs from Redis map");

        Set<Map.Entry<String, String>> entries = redisMapRepository.entrySet();
        log.info("Retrieved {} key-value pairs from Redis map", entries.size());

        return entries;
    }
}