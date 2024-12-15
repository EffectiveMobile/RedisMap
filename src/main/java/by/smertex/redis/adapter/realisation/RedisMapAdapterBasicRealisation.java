package by.smertex.redis.adapter.realisation;

import by.smertex.redis.exception.RedisMapAdapterException;
import redis.clients.jedis.Jedis;

public class RedisMapAdapterBasicRealisation extends AbstractRedisMapAdapter {

    public RedisMapAdapterBasicRealisation(Jedis pool) {
        super(pool);
    }

    public RedisMapAdapterBasicRealisation(String host, int port) {
        super(new Jedis(host, port));
        getJedis().connect();
    }

    public RedisMapAdapterBasicRealisation(String host, int port, String password) {
        super(new Jedis(host, port));
        try{
            getJedis().auth(password);
            getJedis().connect();
        } catch (Exception e) {
            throw new RedisMapAdapterException(e.getMessage());
        }
    }
}
