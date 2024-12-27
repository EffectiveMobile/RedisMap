package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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

    private final String URL = "localhost";

    /**
     * экземпляр JedisPool присоединённый к 127.0.0.1 на пору 6379
     */
    private final JedisPoolConfig poolConfig = new JedisPoolConfig();

    private final JedisPool jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);

    private final Jedis jedis = jedisPool.getResource();

    /**
     * Метод показывает количество записей в бд
     *
     * @return int значение в виде количества записей в бд
     */
    @Override
    public int size() { //  return (int) jedis.getResource().dbSize();

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = pool.getResource();

            pool.returnObject(jedisLocal);
            return (int) jedisLocal.dbSize();
        }
    }

    /**
     * Выдает информацию, о том, что пуста ли бд или нет
     *
     * @return true если пуста иначе false
     */
    @Override
    public boolean isEmpty() { //  return jedis.dbSize() == 0L;

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            boolean res = jedisLocal.dbSize() == 0L;

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Метод позволяет проверить наличие значения по ключу
     *
     * @param key собственно ключ типа String
     * @return true если усть значение иначе false
     */
    @Override
    public boolean containsKey(Object key) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            boolean res = jedisLocal.exists(key.toString());

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Не реализовано
     *
     * @param value value whose presence in this map is to be tested
     * @return true или false
     */
    @Override
    public boolean containsValue(Object value) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            Collection<String> strings = this.values();

            boolean res = strings.contains(value.toString());

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Метод нужен для получения значения по ключу
     *
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String get(Object key) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            String res = jedisLocal.get(key.toString());

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Метод положить в бд по ключу значение
     *
     * @param key   собственно ключ типа String
     * @param value String типзначения
     * @return String тип возвращаемого значения
     */
    @Override
    public String put(String key, String value) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            String res = jedisPool.getResource().set(key.getBytes(), value.getBytes());

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Метод позволяет удалить значение по ключу
     *
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String remove(Object key) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();
            String res = jedisPool.getResource().del(key.toString()) == 0 ? "0" : "1";
            pool.returnObject(jedisLocal);

            return res;
        }
    }

    /**
     * Метод позволяет положит Map в бд
     *
     * @param m собственно мапа
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            for (Entry<? extends String, ?> iter : m.entrySet()) {
                jedisLocal.mset(iter.getKey(), (String) iter.getValue());
            }

            pool.returnObject(jedisLocal);
        }
    }

    /**
     * Метод служит для очистки бд
     */
    @Override
    public void clear() {
        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();


            jedisPool.getResource().flushAll();
            pool.returnObject(jedisLocal);
        }
    }

    @Override
    public Set<String> keySet() {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            Set<String> res = jedisLocal.keys("*");

            System.out.println("Res " + res);

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    @Override
    public Collection<String> values() {

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            Collection<String> res = this.keySet().stream().map(jedisLocal::get).collect(Collectors.toSet());

            pool.returnObject(jedisLocal);

            return res;
        }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {

        return new HashSet<>();
    }
}
