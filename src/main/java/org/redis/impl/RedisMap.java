package org.redis.impl;

import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Реализация {@link Map} для работы с Redis через Jedis.
 * Ключи и значения — строки.
 *
 * @author Мельников Никита
 */
public class RedisMap implements Map<String, String> {

    private final Jedis jedis;

    /**
     * Создает {@link RedisMap}, подключаясь к Redis по указанному хосту и порту.
     *
     * @param host хост Redis.
     * @param port порт Redis.
     */
    public RedisMap(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    /**
     * Возвращает количество элементов в Redis.
     *
     * @return количество элементов.
     */
    @Override
    public int size() {
        return (int) jedis.dbSize();
    }

    /**
     * Проверяет, пуст ли Redis.
     *
     * @return true, если Redis пуст, false в противном случае.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Проверяет, существует ли ключ в Redis.
     *
     * @param key ключ для проверки.
     * @return true, если ключ существует.
     */
    @Override
    public boolean containsKey(Object key) {
        return jedis.exists((String) key);
    }

    /**
     * Проверяет, существует ли значение в Redis.
     *
     * @param value значение для проверки.
     * @return true, если значение существует.
     */
    @Override
    public boolean containsValue(Object value) {
        Set<String> keys = jedis.keys("*");
        if (keys.isEmpty()) {
            return false;
        }

        String[] keysArray = keys.toArray(new String[0]);
        List<String> values = jedis.mget(keysArray);
        return values.contains(value);
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key ключ.
     * @return значение или null, если ключ не существует.
     */
    @Override
    public String get(Object key) {
        return jedis.get((String) key);
    }

    /**
     * Добавляет или обновляет значение по ключу.
     *
     * @param key   ключ.
     * @param value значение.
     * @return предыдущее значение или null, если ключ не существовал.
     */
    @Override
    public String put(String key, String value) {
        String previousValue = get(key);
        jedis.set(key, value);
        return previousValue;
    }

    /**
     * Удаляет ключ и его значение из Redis.
     *
     * @param key ключ для удаления.
     * @return удаленное значение или null, если ключ не существовал.
     */
    @Override
    public String remove(Object key) {
        String value = get(key);
        jedis.del((String) key);
        return value;
    }

    /**
     * Добавляет все элементы из переданной карты в Redis.
     *
     * @param m Map для добавления.
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null || m.isEmpty()) {
            return;
        }

        String[] keyValuePairs = new String[m.size() * 2];

        int index = 0;
        for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
            keyValuePairs[index++] = entry.getKey();
            keyValuePairs[index++] = entry.getValue();
        }
        jedis.mset(keyValuePairs);
    }

    /**
     * Очищает все данные в Redis.
     */
    @Override
    public void clear()  {
        jedis.flushDB();
    }

    /**
     * Возвращает набор всех ключей в Redis.
     *
     * @return набор ключей.
     */
    @Override
    public Set<String> keySet() {
        return jedis.keys("*");
    }

    /**
     * Возвращает коллекцию всех значений в Redis.
     *
     * @return коллекция значений.
     */
    @Override
    public Collection<String> values() {
        Set<String> keys = keySet();
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        String[] keysArray = keys.toArray(new String[0]);
        return jedis.mget(keysArray);
    }

    /**
     * Возвращает набор всех пар ключ-значение в Redis.
     *
     * @return набор пар ключ-значение.
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<String> keys = keySet();
        if (keys.isEmpty()) {
            return Collections.emptySet();
        }

        String[] keysArray = keys.toArray(new String[0]);
        List<String> values = jedis.mget(keysArray);
        Set<Entry<String, String>> entries = new HashSet<>();

        int index = 0;
        for (String key : keys) {
            entries.add(new AbstractMap.SimpleEntry<>(key, values.get(index++)));
        }

        return entries;
    }
}
