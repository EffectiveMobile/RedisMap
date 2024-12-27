package by.smertex.redis.adapter.realisation;

import by.smertex.redis.adapter.interfaces.RedisMapAdapter;
import by.smertex.redis.exception.RedisMapAdapterException;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractRedisMapAdapter implements RedisMapAdapter {
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
    public long redisSize() {
        try {
            return jedis.dbSize();
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return jedis.dbSize() == 0;
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return jedis.exists(key.toString());
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            Object result = jedis.eval(CONTAINS_LUA, 0, value.toString());
            return "1".equals(result.toString());
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public String get(Object key) {
        try {
            return jedis.get(key.toString());
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public String put(String key, String value) {
        try {
            return jedis.set(key, value);
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public String remove(Object key) {
        try {
            String value = jedis.get(key.toString());
            jedis.del(key.toString());
            return value;
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        try {
            m.keySet().forEach(k -> jedis.set(k, m.get(k)));
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public void clear() {
        try {
            jedis.flushDB();
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet() {
        try {
            return jedis.keys("*");
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public Collection<String> values() {
        try {
            return jedis.mget(keySet().toArray(new String[0]));
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        try {
            return keySet().stream()
                    .map(k -> new AbstractRedisMapAdapter.EntryRedis(k, get(k)))
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    final class EntryRedis implements Entry<String, String>{

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
    public Jedis getJedis() {
        return jedis;
    }

    @Override
    public boolean isConnected() {
        return jedis.isConnected();
    }

    @Override
    public void close() {
        try {
            jedis.close();
        } catch (Exception e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    public AbstractRedisMapAdapter(Jedis jedis) {
        this.jedis = jedis;
    }
}
