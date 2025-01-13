package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    public boolean isEmpty() {

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

        boolean res = false;

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            res = jedisLocal.exists(key.toString());

            pool.returnObject(jedisLocal);


        } catch (RuntimeException e) {
            e.printStackTrace();
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

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            Collection<String> strings = this.values();

            res = strings.contains(value.toString());

            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
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

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            res = jedisLocal.get(key.toString());

            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return res;
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

        String res = "";

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            res = jedisLocal.set(key.getBytes(), value.getBytes());

            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return res;
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

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            res = jedisPool.getResource().del(key.toString()) == 0 ? "0" : "1";
            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return res;
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

            String[] set = m.entrySet().stream().flatMap(t-> Stream.of(t.getKey(), t.getValue())).toArray(String[]::new);

            jedisLocal.mset(set);

            pool.returnObject(jedisLocal);
        } catch (RuntimeException e) {
            e.printStackTrace();
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
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> keySet() {

        Set<String> res = new HashSet<>();

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

            res = jedisLocal.keys("*");

            System.out.println("Res " + res);

            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public Collection<String> values() {

        Collection<String> res = new HashSet<>();

        try (JedisPool pool = new JedisPool(URL, 6379)) {

            Jedis jedisLocal = new Jedis();

            jedisLocal.connect();
            jedisLocal = pool.getResource();

          //  res = jedisLocal.mget("*");

            res = this.keySet().stream().map(jedisLocal::get).collect(Collectors.toSet());
            //  List<String> strings = jedisLocal.hget();

            // List<String> result = jedisLocal.mget(.stream());
            //   System.out.println("result " + strings);

            pool.returnObject(jedisLocal);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {

        return new HashSet<>();
    }
}
