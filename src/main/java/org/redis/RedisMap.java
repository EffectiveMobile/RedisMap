package org.redis;

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
        try (Jedis jedis = jedisPool.getResource()) {
            return Math.toIntExact(jedis.dbSize());
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
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key.toString());
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                for (String key : scanResult.getResult()) {
                    if (value.toString().equals(jedis.get(key))) {
                        return true;
                    }
                }
            } while (!"0".equals(cursor));
        }
        return false;
    }


    @Override
    public String get(Object key) {
        if (key == null) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key.toString());
        }
    }


    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key.toString());
            jedis.del(key.toString());
            return value;
        }
    }


    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null || m.isEmpty()) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {

            jedis.mset(m.entrySet().stream()
                    .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                    .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                    .toArray(String[]::new));
        }
    }


    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        try (Jedis jedis = jedisPool.getResource()) {
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                keys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));
        }
        return keys;
    }


    @Override
    public Collection<String> values() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = keySet();

            List<String> results = jedis.mget(keys.toArray(new String[0]));
            return results.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }



    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            return keySet().stream()
                    .map(key -> new AbstractMap.SimpleEntry<>(key, jedis.get(key)))
                    .collect(Collectors.toSet());
        }
    }

}
