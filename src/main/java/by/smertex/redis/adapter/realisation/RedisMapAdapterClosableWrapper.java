package by.smertex.redis.adapter.realisation;

import by.smertex.redis.exception.RedisMapAdapterException;
import redis.clients.jedis.Jedis;

import java.io.Closeable;

public class RedisMapAdapterClosableWrapper extends AbstractRedisMapAdapter
        implements Closeable {

    public RedisMapAdapterClosableWrapper(String host, int port) {
        super(new Jedis(host, port));
        try {
            getJedis().connect();
        } catch (Exception e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    public RedisMapAdapterClosableWrapper(String host, int port, String password) {
        super(new Jedis(host, port));
        try {
            getJedis().auth(password);
            getJedis().connect();
        } catch (Exception e){
            throw new RedisMapAdapterException(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            getJedis().close();
        } catch (Exception e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }
}
