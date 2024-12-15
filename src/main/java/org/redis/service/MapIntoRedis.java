package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс пытается сделать мапу из мапы
 */
@RequiredArgsConstructor
public class MapIntoRedis implements Map<String, String> {

    /**
     * экземпляр JedisPool присоединённый к 127.0.0.1 на пору 6379
     */
    private final JedisPool jedis = new JedisPool("127.0.0.1",6379);

    /**
     * Метод показывает количество записей в бд
     * @return int значение в виде количества записей в бд
     */
    @Override
    public int size() {

        return (int) jedis.getResource().dbSize();
    }

    /**
     * Выдает информацию, о том, что пуста ли бд или нет
     * @return true если пуста иначе false
     */
    @Override
    public boolean isEmpty() {

        return jedis.getResource().dbSize() == 0L;
    }

    /**
     * Метод позволяет проверить наличие значения по ключу
     * @param key собственно ключ типа String
     * @return true если усть значение иначе false
     */
    @Override
    public boolean containsKey(Object key) {

        return jedis.getResource().exists(key.toString());
    }

    /**
     * Не реализовано
     * @param value value whose presence in this map is to be tested
     * @return true или false
     */
    @Override
    public boolean containsValue(Object value) {

            // todo не реализовано

        return false;
    }

    /**
     * Метод нужен для получения значения по ключу
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String get(Object key) {

        return jedis.getResource().get(key.toString());
    }

    /**
     * Метод положить в бд по ключу значение
     * @param key собственно ключ типа String
     * @param value String типзначения
     * @return String тип возвращаемого значения
     */
    @Override
    public String put(String key, String value) {

        return jedis.getResource().set(key.getBytes(), value.getBytes());
    }

    /**
     * Метод позволяет удалить значение по ключу
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String remove(Object key) {

       jedis.getResource().del(key.toString());
       return "";
    }

    /**
     * Метод позволяет положит Map в бд
     * @param m собственно мапа
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

        for (Entry<? extends String, ?> iter : m.entrySet()) {
            jedis.getResource().set(iter.getKey(), (String) iter.getValue());
        }
    }

    /**
     * Метод служит для очистки бд
     */
    @Override
    public void clear() {

        jedis.getResource().flushAll();
    }

    @Override
    public Set<String> keySet() {

        return jedis.getResource().keys("*");
    }

    @Override
    public Collection<String> values() {

        return this.keySet().stream().map(j->jedis.getResource().get(j)).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<String, String>> entrySet() {

        return new HashSet<>();
    }
}
