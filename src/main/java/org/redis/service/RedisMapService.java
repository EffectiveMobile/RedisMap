package org.redis.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Service interface for Redis key-value operations.
 */
public interface RedisMapService {
    int size();

    boolean isEmpty();

    boolean containsKey(String key);

    boolean containsValue(String value);

    String get(String key);

    String put(String key, String value);

    String remove(String key);

    void putAll(Map<String, String> map);

    void clear();

    Set<String> keySet();

    Collection<String> values();

    Set<Map.Entry<String, String>> entrySet();
}

