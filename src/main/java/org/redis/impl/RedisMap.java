package org.redis.impl;

import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisMap implements Map<String, String> {

    private final Jedis jedis;

    public RedisMap(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    @Override
    public int size() {
        return (int) jedis.dbSize();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return jedis.exists((String) key);
    }

    @Override
    public boolean containsValue(Object value) {
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            if (jedis.get(key).equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        return jedis.get((String) key);
    }

    @Override
    public String put(String key, String value) {
        String previousValue = get(key);
        jedis.set(key, value);
        return previousValue;
    }

    @Override
    public String remove(Object key) {
        String value = get(key);
        jedis.del((String) key);
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        jedis.flushDB();
    }

    @Override
    public Set<String> keySet() {
        return jedis.keys("*");
    }

    @Override
    public Collection<String> values() {
        Set<String> keys = keySet();
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            values.add(get(key));
        }
        return values;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<String> keys = keySet();
        Set<Entry<String, String>> entries = new HashSet<>();
        for (String key : keys) {
            entries.add(new AbstractMap.SimpleEntry<>(key, get(key)));
        }
        return entries;
    }
}
