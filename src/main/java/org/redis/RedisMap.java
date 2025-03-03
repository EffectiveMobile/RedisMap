package org.redis;

import redis.clients.jedis.Jedis;
import java.util.*;

/**
 * Реализация интерфейса {@link Map}, которая использует Redis для хранения данных.
 */
public class RedisMap implements Map<String, String> {
    private final Jedis jedis;
    private final String mapName;


    /**
     * Создает новую мапу в Redis с указанным именем и подключается к Redis по указанному хосту и порту.
     *
     * @param mapName Имя мапы.
     * @param host    Хост Redis.
     * @param port    Порт Redis.
     */
    public RedisMap(String mapName, String host, int port) {
        this.mapName = mapName;
        this.jedis = new Jedis(host, port);
    }

    /**
     * Создает новую мапу в Redis с указанным именем и подключается к Redis на localhost:6379.
     *
     * @param mapName Имя мапы.
     */
    public RedisMap(String mapName) {
        this.mapName = mapName;
        this.jedis = new Jedis("localhost", 6379);
    }

    @Override
    public int size() {
        return jedis.keys(mapName + ":*").size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Ключ не может быть null");
        }
        return jedis.exists(getRedisKey(key.toString()));
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Значение не может быть null");
        }
        for (String key : jedis.keys(mapName + ":*")) {
            if (jedis.get(key).equals(value.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Ключ не может быть null");
        }
        return jedis.get(getRedisKey(key.toString()));
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("Ключ и значение не могут быть null");
        }
        String redisKey = getRedisKey(key);
        String oldValue = jedis.get(redisKey);
        jedis.set(redisKey, value);
        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Ключ не может быть null");
        }
        String redisKey = getRedisKey(key.toString());
        String oldValue = jedis.get(redisKey);
        jedis.del(redisKey);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (String key : jedis.keys(mapName + ":*")) {
            jedis.del(key);
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (String redisKey : jedis.keys(mapName + ":*")) {
            keys.add(redisKey.substring(mapName.length() + 1));
        }
        return keys;
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        for (String redisKey : jedis.keys(mapName + ":*")) {
            values.add(jedis.get(redisKey));
        }
        return values;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> entries = new HashSet<>();
        for (String redisKey : jedis.keys(mapName + ":*")) {
            String key = redisKey.substring(mapName.length() + 1);
            String value = jedis.get(redisKey);
            entries.add(new AbstractMap.SimpleEntry<>(key, value));
        }
        return entries;
    }

    /**
     * Генерирует ключ для Redis, добавляя имя мапы.
     *
     * @param key Ключ мапы.
     * @return Ключ для Redis.
     */
    private String getRedisKey(String key) {
        return mapName + ":" + key;
    }

    /**
     * Закрывает соединение с Redis.
     */
    public void close() {
        jedis.close();
    }
}