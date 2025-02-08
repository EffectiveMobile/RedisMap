package org.redis.impl;

import org.redis.exception.RedisMapException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link Map} на основе Redis.
 * Все ключи хранятся с заданным префиксом, чтобы избежать конфликтов с другими данными в Redis.
 */
public class RedisMap implements Map<String, String> {

    private final JedisPool jedisPool;
    private final String prefix;

    public RedisMap(JedisPool jedisPool, String prefix) {
        this.jedisPool = Objects.requireNonNull(jedisPool);
        this.prefix = Objects.requireNonNull(prefix) + ":";
    }

    /**
     * Создание ключа с учетом префикса.
     *
     * @param key - переданный ключ.
     * @return - актуальный ключ.
     */
    private String actualKey(String key) {
        return prefix + key;
    }

    /**
     * Метод для вычисления количества элементов в Redis.
     *
     * @return - количество элементов в Redis.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public int size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(actualKey("*")).size();
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверка пустой ли Redis.
     *
     * @return - {@code true} - redis пуст, {@code false} - redis не пуст
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Проверяет, существует ли переданный ключ в redis.
     *
     * @param key - ключ для проверки.
     * @return - {@code true} - ключ существует, {@code false} - ключ не существует.
     * @throws NullPointerException - выбрасывается при передаче пустого ключа.
     * @throws ClassCastException - выбрасывается при передаче ключа с типом не String.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public boolean containsKey(Object key) {
        if(key == null){
            throw new NullPointerException("key is null");
        }

        if(!(key instanceof String)) {
            throw new ClassCastException("Key not instance of String");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(actualKey((String) key));
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверяет, существует ли переданное значение в redis.
     *
     * @param value - значение для проверки.
     * @return - {@code true} - значение существует, {@code false} - значение не существует.
     * @throws ClassCastException - выбрасывается при передаче значения с типом не String.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) {
            throw new ClassCastException("Value not instance of String");
        }
        return values().contains((String) value);
    }

    /**
     * Получение значения по ключу.
     *
     * @param key - ключ для получения значения.
     * @return - значение по переданному ключу.
     * @throws NullPointerException - выбрасывается при передаче пустого ключа.
     * @throws ClassCastException - выбрасывается при передаче ключа с типом не String.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public String get(Object key) {
        if(key == null){
            throw new NullPointerException("key is null");
        }

        if(!(key instanceof String)) {
            throw new ClassCastException("Key not instance of String");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(actualKey((String) key));
        }catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Добавляет в Redis пару ключ-значение.
     * Если ключ уже существует, метод перезаписывает значение и возвращает предыдущее.
     *
     * @param key - ключ.
     * @param value - новое значение.
     * @return - предыдущее значение или {@code null}, если ключ ранее отсутствовал.
     * @throws NullPointerException - если {@code key} равен {@code null}.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public String put(String key, String value) {
        if(key == null){
            throw new NullPointerException("Key is null");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getSet(actualKey(key), value);
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет элемент из Redis по ключу.
     *
     * @param key - ключ для удаления.
     * @return - предыдущее значение или {@code null}, если ключ отсутствовал.
     * @throws ClassCastException - если {@code key} не является строкой.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public String remove(Object key) {
        if(key == null){
            throw new NullPointerException("key is null");
        }

        if(!(key instanceof String)) {
            throw new ClassCastException("Key not instance of String");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            String actualKey = actualKey((String) key);
            String value = jedis.get(actualKey);
            jedis.del(actualKey);
            return value;
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Добавляет все элементы из переданной мапы в Redis.
     *
     * @param map - мапа с парами ключ-значение для добавления.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        try (Jedis jedis = jedisPool.getResource()) {
            map.forEach((k, v) -> jedis.mset(actualKey(k), v));
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет все пары ключ-значение.
     *
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(actualKey("*"));
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Получение множество всех хранящихся ключей.
     *
     * @return - множество ключей.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public Set<String> keySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(actualKey("*")).stream()
                    .map(k -> k.substring(prefix.length()))
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Получение коллекции со всеми значениями.
     *
     * @return - коллекция значений.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public Collection<String> values() {

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(actualKey("*"));
            return jedis.mget(keys.toArray(new String[0]));
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Получение множество всех пар ключ-значение.
     *
     * @return - множество пар ключ-значение.
     * @throws RedisMapException - в случае ошибки работы с Redis.
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(actualKey("*"));
            return keys.stream()
                    .map(k -> new AbstractMap.SimpleEntry<>(k.substring(prefix.length()), jedis.get(k)))
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }
}
