package org.redis.service;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MapIntoRedis implements Map<String, String> {

    private final JedisPool jedis = new JedisPool("127.0.0.1",6379);

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

        //return !jedis.getResource().keys(String.valueOf(value)).isEmpty();

      //  Object o = jedis.getResource().eval(jedis.getResource().get("*"), 0, value.toString());

        return false;
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
            jedis.getResource().set(iter.getKey(), (String) iter.getValue());
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

        return new HashSet<>();
    }
}
