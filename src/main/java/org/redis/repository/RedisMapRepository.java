package org.redis.repository;

import org.redis.exception.RedisMapOperationException;
import org.redis.exception.handler.RedisMapErrorHandler;
import org.redis.util.config.RedisMapConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Repository for managing key-value pairs in Redis, implementing {@link Map}.
 */
public class RedisMapRepository implements Map<String, String> {
    private final JedisPool jedisPool;
    private final RedisMapErrorHandler errorHandler;

    public RedisMapRepository(RedisMapErrorHandler errorHandler) {
        this.jedisPool = RedisMapConfig.getJedisPool();
        this.errorHandler = errorHandler;
    }

    /**
     * Returns the number of entries in Redis.
     */
    @Override
    public int size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return Math.toIntExact(jedis.dbSize());
        } catch (Exception ex) {
            errorHandler.handleError("Failed to get size from Redis", ex, RedisMapOperationException.class);
            return 0;
        }
    }

    /**
     * Checks if Redis is empty.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Checks if the given key exists in Redis.
     */
    @Override
    public boolean containsKey(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key.toString());
        } catch (Exception ex) {
            errorHandler.handleError("Failed to check key existence in Redis", ex, RedisMapOperationException.class);
            return false;
        }
    }

    /**
     * Checks if the given value exists in Redis.
     */
    @Override
    public boolean containsValue(Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            for (String key : keys) {
                if (Objects.equals(jedis.get(key), value)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            errorHandler.handleError("Failed to check value existence in Redis", ex, RedisMapOperationException.class);
            return false;
        }
    }

    /**
     * Retrieves the value for the given key.
     */
    @Override
    public String get(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key.toString());
        } catch (Exception ex) {
            errorHandler.handleError("Failed to get value from Redis", ex, RedisMapOperationException.class);
            return null;
        }
    }

    /**
     * Adds or updates a key-value pair in Redis.
     */
    @Override
    public String put(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String oldValue = get(key);
            jedis.set(key, value);

            return oldValue;
        } catch (Exception ex) {
            errorHandler.handleError("Failed to put value into Redis", ex, RedisMapOperationException.class);
            return null;
        }
    }

    /**
     * Removes a key from Redis.
     */
    @Override
    public String remove(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String oldValue = get(key);
            jedis.del(key.toString());

            return oldValue;
        } catch (Exception ex) {
            errorHandler.handleError("Failed to remove value from Redis", ex, RedisMapOperationException.class);
            return null;
        }
    }

    /**
     * Adds multiple key-value pairs to Redis.
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        try {
            for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        } catch (Exception ex) {
            errorHandler.handleError("Failed to put all values into Redis", ex, RedisMapOperationException.class);
        }
    }

    /**
     * Clears all entries in Redis.
     */
    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        } catch (Exception ex) {
            errorHandler.handleError("Failed to clear Redis", ex, RedisMapOperationException.class);
        }
    }

    /**
     * Returns all keys in Redis.
     */
    @Override
    public Set<String> keySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys("*");
        } catch (Exception ex) {
            errorHandler.handleError("Failed to get keys from Redis", ex, RedisMapOperationException.class);
            return Collections.emptySet();
        }
    }

    /**
     * Returns all values in Redis.
     */
    @Override
    public Collection<String> values() {
        try {
            Set<String> keys = keySet();
            List<String> values = new ArrayList<>();
            for (String key : keys) {
                values.add(get(key));
            }
            return values;
        } catch (Exception ex) {
            errorHandler.handleError("Failed to get values from Redis", ex, RedisMapOperationException.class);
            return Collections.emptyList();
        }
    }

    /**
     * Returns all key-value entries in Redis.
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        try {
            Set<String> keys = keySet();
            Set<Entry<String, String>> entries = new HashSet<>();
            for (String key : keys) {
                entries.add(new AbstractMap.SimpleEntry<>(key, get(key)));
            }
            return entries;
        } catch (Exception ex) {
            errorHandler.handleError("Failed to get entries from Redis", ex, RedisMapOperationException.class);
            return Collections.emptySet();
        }
    }
}