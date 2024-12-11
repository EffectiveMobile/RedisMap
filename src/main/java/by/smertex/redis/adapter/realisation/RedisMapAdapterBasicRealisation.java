package by.smertex.redis.adapter.realisation;

import by.smertex.redis.adapter.interfaces.RedisMapAdapter;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

public final class RedisMapAdapterBasicRealisation implements RedisMapAdapter {

    private static final String CONTAINS_LUA = """
                                               for _, key in ipairs(redis.call('keys', '*')) do
                                                    if redis.call('get', key) == ARGV[1] then
                                                        return 1
                                                    end
                                               end
                                               return 0
                                               """;

    private final Jedis jedis;

    @Override
    public int size() {
        try {
            return (int) jedis.dbSize();
        } catch (ClassCastException e) {
            return -1;
        }
    }

    @Override
    public boolean isEmpty() {
        return jedis.dbSize() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return jedis.exists(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        Object result = jedis.eval(CONTAINS_LUA, 0, value.toString());
        return "1".equals(result.toString());
    }

    @Override
    public String get(Object key) {
        return jedis.get(key.toString());
    }

    @Override
    public String put(String key, String value) {
        return jedis.set(key, value);
    }

    @Override
    public String remove(Object key) {
        String value = jedis.get(key.toString());
        jedis.del(key.toString());
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        m.keySet().forEach(k -> jedis.set(k, m.get(k)));
    }

    @Override
    public void clear() {
        jedis.flushDB();
    }

    @Override
    public Set<String> keySet() {
        return jedis.keys("*");
    }

    @Override
    public Collection<String> values() {
        return keySet().stream()
                .map(jedis::get)
                .toList();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return keySet().stream()
                .map(k -> new EntryRedis(k, get(k)))
                .collect(Collectors.toSet());
    }

    private final class EntryRedis implements Entry<String, String>{

        private final String key;

        private String value;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            this.value = value;
            put(this.key, value);
            return this.value;
        }

        public EntryRedis(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void close() {
        jedis.close();
    }

    @Override
    public boolean isConnected() {
        return jedis.isConnected();
    }

    public RedisMapAdapterBasicRealisation(Jedis jedis) {
        this.jedis = jedis;
    }

    public RedisMapAdapterBasicRealisation(String host, int port) {
        this.jedis = new Jedis(host, port);
        jedis.connect();
    }

    public RedisMapAdapterBasicRealisation(String host, int port, String password) {
        this.jedis = new Jedis(host, port);
        jedis.auth(password);
        jedis.connect();
    }
}
