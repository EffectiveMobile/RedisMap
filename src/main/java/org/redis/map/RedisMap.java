package org.redis.map;

import org.redis.exception.RedisMapException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.ScanResult;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisMap implements Map<String, String> {
    private final JedisPool pool;

    public RedisMap(JedisPool pool) {
        this.pool = pool;
    }

    /**
     * Возвращает количество элементов в базе данных.
     *
     * @return количество элементов
     */
    @Override
    public int size() {
        try (Jedis jedis = pool.getResource()) {
            return Math.toIntExact(jedis.dbSize());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверяет базу данных на наличие элементов.
     *
     * @return {@code true} - если нет ключей, иначе - {@code false}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Проверяет базу данных на наличие элемента с ключом {@code key}.
     *
     * @param key ключ, наличие в базе данных которого проверяется
     * @return {@code true} если ключ существует, иначе - {@code false}
     */
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        try (Jedis jedis = pool.getResource()) {
            return jedis.exists(key.toString());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Проверяет базу данных на наличие элемента со значением {@code value}.
     *
     * @param value значение, наличие в базе данных которого проверяется
     * @return {@code true} если значение существует, иначе - {@code false}
     */
    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        try (Jedis jedis = pool.getResource()) {
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getCursor();
                String[] res = scanResult.getResult()
                        .toArray(new String[0]);

                List<String> values = jedis.mget(res);
                if (values.contains(value.toString())) {
                    return true;
                }
            } while (!"0".equals(cursor));
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
        return false;
    }

    /**
     * Возвращает строку ассоциирующийся со значением ключа {@code key}.
     *
     * @param key ключ, ассоциирующийся со значением, которое нужно вернуть
     * @return значение, хранящееся под этим ключом
     */
    @Override
    public String get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key can not be null!");
        }
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key.toString());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Устанавливает значения {@code value} для ключа {@code key}.
     *
     * @param key   ключ, с которым будет ассоциироваться указанное значение
     * @param value значение, которое будет ассоциировано с указанным ключом
     * @return новое значение для вставки в базе данных
     */
    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null");
        }
        try (Jedis jedis = pool.getResource()) {
            return jedis.set(key, value);
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет элемент с ключом {@code key} из базы данных.
     *
     * @param key ключ для доступа к элементу
     * @return значение удаленного элемента
     */
    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key can not be null!");
        }
        try (Jedis jedis = pool.getResource()) {
            return String.valueOf(jedis.getDel(key.toString()));
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Копирует все элементы {@link Map} в базу данных.
     *
     * @param m {@link Map} для передачи в базу данных
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null || m.isEmpty()) {
            return;
        }
        try (Jedis jedis = pool.getResource()) {
            String[] array = m.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null
                            && entry.getValue() != null
                    )
                    .flatMap(entry -> Stream.of(
                            entry.getKey(),
                            entry.getValue()
                    ))
                    .toArray(String[]::new);

            jedis.mset(array);
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Удаляет все элементы в базе данных.
     */
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushDB();
        }
    }

    /**
     * Возвращает набор ключей базы данных.
     *
     * @return все ключи элементов
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
     * Возвращает набор значений базы данных.
     *
     * @return все значения элементов
     */
    @Override
    public Collection<String> values() {
        try (Jedis jedis = pool.getResource()) {
            String[] keys = keySet().toArray(new String[0]);
            return keys.length != 0
                    ? jedis.mget(keys)
                    : Collections.emptyList();
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }

    /**
     * Возвращает набор пар вида {@code key=value}, содержащихся в базе данных.
     *
     * @return набор пар вида {@code key=value}
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = pool.getResource()) {
            return keySet().stream()
                    .map(key -> new AbstractMap.SimpleEntry<>(key, jedis.get(key)))
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw new RedisMapException(e.getMessage());
        }
    }
}
