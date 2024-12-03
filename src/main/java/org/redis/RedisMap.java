package org.redis;

import redis.clients.jedis.JedisPool;

import java.util.*;

public class RedisMap implements Map<String,String> {
    private final RedisExecutor executor;

    public RedisMap(JedisPool jedisPool) {
        this.executor = new RedisExecutor(jedisPool);
    }


    @Override
    public int size() {
        return executor.execute(jedis -> (int) jedis.dbSize());
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return executor.execute(jedis -> jedis.exists((String) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return executor.execute(jedis -> jedis.keys("*")
                .stream()
                .anyMatch(key -> jedis.get(key).equals(value)));
    }

    @Override
    public String get(Object key) {
        return executor.execute(jedis -> jedis.get((String) key));
    }

    @Override
    public String put(String key, String value) {
        return executor.execute(jedis -> {
            String oldValue = jedis.get(key);
            jedis.set(key, value);
            return oldValue;
        });
    }

    @Override
    public String remove(Object key) {
        return executor.execute(jedis -> {
            String oldValue = jedis.get((String) key);
            jedis.del((String) key);
            return oldValue;
        });
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        executor.execute(jedis -> {
            m.forEach(jedis::set);
            return null;
        });
    }

    @Override
    public void clear() {
        executor.execute(jedis -> {
            jedis.flushDB();
            return null;
        });
    }

    @Override
    public Set<String> keySet() {
        return executor.execute(jedis -> jedis.keys("*"));
    }

    @Override
    public Collection<String> values() {
        return executor.execute(jedis -> {
            Set<String> keys = jedis.keys("*");
            List<String> values = new ArrayList<>();
            for (String key : keys) {
                values.add(jedis.get(key));
            }
            return values;
        });
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return executor.execute(jedis -> {
            Set<String> keys = jedis.keys("*");
            Set<Entry<String, String>> entrySet = new HashSet<>();
            for (String key : keys) {
                entrySet.add(new AbstractMap.SimpleEntry<>(key, jedis.get(key)));
            }
            return entrySet;
        });
    }
}
