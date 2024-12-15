package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapIntoRedis implements Map<String, String> {

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

        return jedis.getResource().exists(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {

        return !jedis.getResource().keys(String.valueOf(value)).isEmpty();
    }

    @Override
    public String get(Object key) {

        return jedis.getResource().get(key.toString());
    }

    @Override
    public String put(String key, String value) {

        return jedis.getResource().set(key.getBytes(), value.getBytes());
    }

    @Override
    public String remove(Object key) {

       jedis.getResource().del(key.toString());
       return "";
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {

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
    public Collection<String> values() {

        return this.keySet().stream().map(j->jedis.getResource().get(j)).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<String, String>> entrySet() {

        return null;
    }
}
