package org.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.stream.Stream;

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
        return executor.execute(jedis -> {
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                List<String> keys = scanResult.getResult();

                if (!keys.isEmpty()) {
                    List<String> values = jedis.mget(keys.toArray(new String[0]));
                    if (values.stream().anyMatch(value::equals)) {
                        return true;
                    }
                }
            } while (!"0".equals(cursor));

            return false;
        });
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
            if (oldValue != null) {
                long deletedCount = jedis.del((String) key);
                if (deletedCount == 0) {
                    return null;
                }
            }
            return oldValue;
        });
    }


    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        executor.execute(jedis -> {
            if (!m.isEmpty()) {
                String[] keyValuePairs = m.entrySet()
                        .stream()
                        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                        .toArray(String[]::new);

                jedis.mset(keyValuePairs);
            }
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
            String cursor = "0";
            List<String> values = new ArrayList<>();

            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                List<String> keys = scanResult.getResult();

                if (!keys.isEmpty()) {
                    values.addAll(jedis.mget(keys.toArray(new String[0])));
                }
            } while (!"0".equals(cursor));

            return values;
        });
    }


    @Override
    public Set<Entry<String, String>> entrySet() {
        return executor.execute(jedis -> {
            String cursor = "0";
            Set<Entry<String, String>> entrySet = new HashSet<>();

            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                List<String> keys = scanResult.getResult();

                if (!keys.isEmpty()) {
                    List<String> values = jedis.mget(keys.toArray(new String[0]));

                    for (int i = 0; i < keys.size(); i++) {
                        entrySet.add(new AbstractMap.SimpleEntry<>(keys.get(i), values.get(i)));
                    }
                }
            } while (!"0".equals(cursor));

            return entrySet;
        });
    }


}
