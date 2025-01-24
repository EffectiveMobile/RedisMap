package org.redis;

import org.redis.exception.CacheDeletionException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisMap implements Map<String, String> {
    private final JedisPool jedisPool;


    public RedisMap(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public int size() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return Math.toIntExact(jedis.dbSize());
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key.toString());
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                List<String> values = jedis.mget(scanResult.getResult().toArray(new String[0]));
                if (values.contains(value.toString())) {
                    return true;
                }
            } while (!"0".equals(cursor));
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return false;
    }



    @Override
    public String get(Object key) {
        if (key == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key.toString());
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null");
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            return value;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.get(key.toString());
            long deleted = jedis.del(key.toString());
            if (deleted == 0) {
                throw new CacheDeletionException("Failed to delete the cache for key: " + key);
            }
            return value;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }



    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null || m.isEmpty()) {
            return;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.mset(m.entrySet().stream()
                    .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                    .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                    .toArray(String[]::new));
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    @Override
    public void clear() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushDB();
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                keys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
        return keys;
    }


    @Override
    public Collection<String> values() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String> keys = keySet();
            List<String> results = jedis.mget(keys.toArray(new String[0]));
            return results.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }



    @Override
    public Set<Entry<String, String>> entrySet() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Jedis finalJedis = jedis;
            return keySet().stream()
                    .map(key -> new AbstractMap.SimpleEntry<>(key, finalJedis.get(key)))
                    .collect(Collectors.toSet());
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

}
