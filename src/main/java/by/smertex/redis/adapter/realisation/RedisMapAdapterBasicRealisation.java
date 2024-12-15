package by.smertex.redis.adapter.realisation;

import redis.clients.jedis.JedisPool;

public final class RedisMapAdapterBasicRealisation extends AbstractRedisMapAdapter {
    public RedisMapAdapterBasicRealisation(JedisPool pool) {
        super(pool);
    }
}
