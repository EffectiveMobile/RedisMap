package org.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Класс-прослойка между Redis и Map
 */
@RequiredArgsConstructor
@Slf4j
public class MapIntoRedis implements Map<String, String> {
    private final String URL = "localhost";

    /**
     * Экземпляр класса JedisPool присоединённый к хосту 127.0.0.1 на пору 6379
     */
    private final JedisPoolConfig poolConfig = new JedisPoolConfig();
    private final JedisPool jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
    private final Jedis jedis = jedisPool.getResource();

    /**
     * Метод возвращает количество записей в БД
     *
     * @return int количество записей в БД
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
     * Дает информацию о том пуста ли БД или нет
     *
     * @return true если пуста иначе false
     */
    @Override
    public boolean isEmpty() {
        boolean res;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            res = jedisLocal.dbSize() == 0L;
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    /**
     * Метод позволяет проверить наличие значения по ключу
     *
     * @param key ключ типа String
     * @return true если присутствует значение иначе false
     */
    @Override
    public boolean containsKey(Object key) {
        boolean res;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            res = jedisLocal.exists(key.toString());
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean res;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            Collection<String> strings = this.values();
            res = strings.contains(value.toString());
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    /**
     * Метод для получения значения по ключу
     *
     * @param key ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String get(Object key) {
        String res = "";
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            res = jedisLocal.get(key.toString());
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    /**
     * Метод для записи в БД значение по его ключу
     *
     * @param key   ключ типа String
     * @param value значение типа String
     * @return возвращаемое значение типа String
     */
    @Override
    public String put(String key, String value) {
        Jedis jedisLocal = null;
        String valueInMap;
        try {
            jedisLocal = jedisPool.getResource();
            valueInMap = jedisLocal.get(key);
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return valueInMap;
    }

    /**
     * Метод позволяет удалить значение по ключу
     *
     * @param key собственно ключ типа String
     * @return String тип возвращаемого значения
     */
    @Override
    public String remove(Object key) {
        String valueInMap;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            valueInMap = jedisLocal.get(key.toString());
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return valueInMap;
    }

    /**
     * Метод позволяет положить Map в БД
     *
     * @param m экземпляр Map
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            String[] set = m.entrySet().stream().flatMap(t -> Stream.of(t.getKey(), t.getValue())).toArray(String[]::new);
            jedisLocal.mset(set);
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
    }

    /**
     * Метод служит для очистки БД
     */
    @Override
    public void clear() {
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            jedisLocal.flushAll();
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> res;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            res = jedisLocal.keys("*");
            log.info("Res {}", res);
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
        } finally {
            jedisPool.returnObject(jedisLocal);
        }
        return res;
    }

    @Override
    public Collection<String> values() {
        Collection<String> res;
        Jedis jedisLocal = null;
        try {
            jedisLocal = jedisPool.getResource();
            jedisLocal.connect();
            jedisLocal = jedisPool.getResource();
            res = jedis.mget(this.keySet().toArray(new String[0]));
        } catch (RuntimeException exception) {
            log.error("Uncaught exception.", exception);
            throw exception;
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
