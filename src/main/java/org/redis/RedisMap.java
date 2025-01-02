package org.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;


public class RedisMap implements Map<String, String> {

    private final JedisPool jedisPool;
    private final int database;

    public RedisMap(JedisPool jedisPool, int database) {
        this.jedisPool = jedisPool;
        this.database = database;
    }

    @Override
    public int size() {
        int resultDbSize = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            resultDbSize = Math.toIntExact(jedis.dbSize());
        } catch (ArithmeticException e) {
            System.out.println("Ошибка");
        }
        return resultDbSize;
    }

    @Override
    public boolean isEmpty() {
        int mapSize = size();
        if (mapSize == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            return jedis.exists(key.toString());
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            String valueArgumentObject = value.toString();
            String cursor = "0";
            String valueFromDB;
            ScanParams scanParams = new ScanParams().match("*").count(10);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> listResult = scanResult.getResult();
                for (String key : listResult) {
                    valueFromDB = jedis.get(key);
                    if (valueArgumentObject != null && valueArgumentObject.equals(valueFromDB)) {
                        return true;
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));
        }
        return false;
    }

    @Override
    public String get(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            return jedis.get(key.toString());
        }
    }

    @Override
    public String put(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            return jedis.set(key, value);
        }
    }

    @Override
    public String remove(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            String keyForDelete = key.toString();
            long result = jedis.del(keyForDelete);
            jedis.save();
            return String.valueOf(result);
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            m.forEach(jedis::set);
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            jedis.flushDB();
        }
    }

    @Override
    public Set<String> keySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            return jedis.keys("*");
        }
    }

    @Override
    public Collection<String> values() {
        Collection<String> collection = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            jedis.keys("*").forEach(key -> collection.add(jedis.get(key)));
            return collection;
        }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.select(database);
            Map<String, String> entrySet = new HashMap<>();
            for (String key : jedis.keys("*")) {
                entrySet.put(key, jedis.get(key));
            }
            return entrySet.entrySet();
        }
    }
}
