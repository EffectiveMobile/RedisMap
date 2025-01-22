package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
    public int size() {

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            return (int) jedisLocal.dbSize();
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
    }

    /**
     * Выдает информацию, о том, что пуста ли бд или нет
     *
     * @return true если пуста иначе false
     */
    @Override
    public boolean isEmpty() {

        boolean res = false;
        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            res = jedisLocal.dbSize() == 0L;

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    /**
     * Метод позволяет проверить наличие значения по ключу
     *
     * @param key собственно ключ типа String
     * @return true если усть значение иначе false
     */
    @Override
    public boolean containsKey(Object key) {

        boolean res = false;

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            res = jedisLocal.exists(key.toString());


        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res;
    }

    /**
     * Не реализовано
     *
     * @param value value whose presence in this map is to be tested
     * @return true или false
     */
    @Override
    public boolean containsValue(Object value) {

        boolean res = false;

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            Collection<String> strings = this.values();

            res = strings.contains(value.toString());

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res;
    }

    /**
     * Метод нужен для получения значения по ключу
     *
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String get(Object key) {

        String res = "";

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            res = jedisLocal.get(key.toString());

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res != null ? res : null;
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

        String res = null;
        Jedis jedisLocal = null;
        String valueInMap = "";

        try {

            jedisLocal = jedisPool.getResource();

            valueInMap = jedisLocal.get(key);

            res = jedisLocal.set(key.getBytes(), value.getBytes());
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res != null ? res : valueInMap;
    }

    /**
     * Метод позволяет удалить значение по ключу
     *
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String remove(Object key) {

        String res = "";
        String valueInMap = "";
        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();
            valueInMap = jedisLocal.get(key.toString());
            res = jedisLocal.del(key.toString()) == 0 ? "0" : "1";

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res != null ? res : valueInMap;
    }

    /**
     * Метод позволяет положит Map в бд
     *
     * @param m собственно мапа
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            String[] set = m.entrySet().stream().flatMap(t -> Stream.of(t.getKey(), t.getValue())).toArray(String[]::new);

            jedisLocal.mset(set);

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }
    }

    /**
     * Метод служит для очистки бд
     */
    @Override
    public void clear() {

        Jedis jedisLocal = null;

        try {
            jedisLocal = jedisPool.getResource();
            jedisLocal.flushAll();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }
    }

    @Override
    public Set<String> keySet() {

        Set<String> res = new HashSet<>();
        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            res = jedisLocal.keys("*");

            System.out.println("Res " + res);

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res;
    }

    @Override
    public Collection<String> values() {

        Collection<String> res = new HashSet<>();

        Jedis jedisLocal = null;

        try {

            jedisLocal = jedisPool.getResource();

            jedisLocal.connect();
            jedisLocal = jedisPool.getResource();

            res = jedis.mget(this.keySet().toArray(new String[0]));

        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            jedisPool.returnObject(jedisLocal);
        }

        return res;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {

        return new HashSet<>();
    }
}
