package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapIntoRedis implements Map<String, Object> {

    private final JedisPool jedis = new JedisPool("127.0.0.1",6379);

    public void pingRedis() throws Exception {

        System.out.println(jedis.borrowObject().ping());
    }

    @Override
    public int size() {

        return (int) jedis.getResource().dbSize();
    }

    @Override
    public boolean isEmpty() {

        return jedis.getResource().dbSize() == 0L;
    }

    @Override
    public boolean containsKey(Object key) {

        return jedis.getResource().exists((byte[]) key);
    }

    @Override
    public boolean containsValue(Object value) {

        return !jedis.getResource().keys(String.valueOf(value)).isEmpty();
    }

    @Override
    public Object get(Object key) {

        return jedis.getResource().get((String) key);
    }

    @Override
    public Object put(String key, Object value) {

        return jedis.getResource().set(key.getBytes(), (byte[]) value);
    }

    @Override
    public Object remove(Object key) {

        return jedis.getResource().del((String) key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {

        for (Entry<? extends String, ?> iter : m.entrySet()) {
            jedis.getResource().set(iter.getKey().getBytes(), (byte[]) iter.getValue());
        }
    }

    @Override
    public void clear() {

        jedis.getResource().flushAll();
    }

    @Override
    public Set<String> keySet() {

        return jedis.getResource().keys("*");
    }

    @Override
    public Collection<Object> values() {

        return this.keySet().stream().map(j->jedis.getResource().get(j)).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {

        return null;
    }
}
