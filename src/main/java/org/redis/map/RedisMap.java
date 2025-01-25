package org.redis.map;

import org.redis.map.exception.RedisMapException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс-прослойка между Redis и интерфейсом Map.
 * @author Даниил Астафьев
 * @version 1.0
 */
public class RedisMap implements Map<String, String> {

    private final JedisPool pool;

    public RedisMap(JedisPool pool) {
        this.pool = pool;
    }

    /**
     * Возвращает количество ключей в текущей базе данных.
     * @return число ключей
     */
    @Override
    public int size() {
        try (Jedis jedis = pool.getResource()) {
            return (int) jedis.dbSize();
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Возвращает {@code true}, если в базе данных нет ключей.
     * @return {@code true} - если нет ключей, иначе - {@code false}
     */
    @Override
    public boolean isEmpty() {
        try (Jedis jedis = pool.getResource()) {
            return jedis.dbSize() == 0;
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверяет, существует ли ключ в базе данных.
     * @param key ключ, наличие в базе данных которого проверяется
     * @return {@code true} если ключ существует, иначе - {@code false}
     */
    @Override
    public boolean containsKey(Object key) {
        try (Jedis jedis = pool.getResource()) {
            if (key != null) {
                return jedis.exists(key.toString());
            } else {
                throw new NullPointerException("Key can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверяет, существует ли значение в базе данных.
     * @param value значение, наличие в базе данных которого проверяется
     * @return {@code true} если значение существует, иначе - {@code false}
     */
    @Override
    public boolean containsValue(Object value) {
        try (Jedis jedis = pool.getResource()) {
            if (value != null) {
                Set<String> keys = jedis.keys("*");
                long res = keys.stream().filter(key -> {
                    String val = jedis.get(key);
                    return val.equals(value.toString());
                }).count();
                return res > 0;
            } else {
                throw new NullPointerException("Value can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Получает значение указанного ключа.
     * @param key ключ, ассоциирующийся со значением, которое нужно вернуть
     * @return значение, хранящееся под этим ключом
     */
    @Override
    public String get(Object key) {
        try (Jedis jedis = pool.getResource()) {
            if (key != null) {
                return jedis.get(key.toString());
            } else {
                throw new NullPointerException("Key can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Устанавливает переданное значение как значение переданного ключа.
     * @param key ключ, с которым будет ассоциироваться указанное значение
     * @param value значение, которое будет ассоциировано с указанным ключом
     * @return OK
     */
    @Override
    public String put(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            if (key != null && value != null) {
                return jedis.set(key, value);
            } else {
                throw new NullPointerException("Key and value can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет указанный ключ из базы данных.
     * @param key ключ, который должен быть удален
     * @return 1 если ключ был удален, 0 если ключа не существует в базе данных
     */
    @Override
    public String remove(Object key) {
        try (Jedis jedis = pool.getResource()) {
            if (key != null) {
                return String.valueOf(jedis.del(key.toString()));
            } else {
                throw new NullPointerException("Key can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Копирует все пары ключ-значение переданной {@code Map} в базу данных.
     * @param m {@code Map}, чьи пары ключ-значение будут сохранены в базе данных
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        try (Jedis jedis = pool.getResource()) {
            if (m != null) {
                String res = m.entrySet()
                        .stream()
                        .map(e -> e.getKey() + "," + e.getValue())
                        .collect(Collectors.joining(","));
                String[] array = res.split(",");
                jedis.mset(array);
            } else {
                throw new NullPointerException("Map can not be null");
            }
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет все ключи текущей базы данных.
     */
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushDB();
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Возвращает набор ключей текущей базы данных.
     * @return набор ключей
     */
    @Override
    public Set<String> keySet() {
        try (Jedis jedis = pool.getResource()) {
            return jedis.keys("*");
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Возвращает все значения текущей базы данных.
     * @return все значения
     */
    @Override
    public Collection<String> values() {
        try (Jedis jedis = pool.getResource()) {
            Set<String> keys = jedis.keys("*");
            String res = String.join(",", keys);
            String[] array = res.split(",");
            return jedis.mget(array);
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Возвращает набор пар вида {@code key=value}, содержащихся в базе данных.
     * @return набор пар вида {@code key=value}
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = pool.getResource()) {
            Set<String> keys = jedis.keys("*");

            return keys.stream()
                    .map(key -> new AbstractMap.SimpleEntry<>(key, jedis.get(key)))
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }
}
