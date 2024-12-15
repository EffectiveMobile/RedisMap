package by.smertex.redis.adapter.realisation;

import by.smertex.redis.exception.RedisMapAdapterException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Closeable;

public class RedisMapAdapterClosableWrapper extends AbstractRedisMapAdapter
        implements Closeable {

    public RedisMapAdapterClosableWrapper(String host, int port) {
        super(new JedisPool(host, port));
    }

    public RedisMapAdapterClosableWrapper(JedisPoolConfig config, String host, int port) {
        super(new JedisPool(config, host, port));
    }

    @Override
    public void close() {
        try {
            getJedisPool().close();
        } catch (Exception e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }
}
