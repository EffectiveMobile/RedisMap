package by.smertex.redis.adapter.realisation;

import redis.clients.jedis.Jedis;

public final class RedisMapAdapterBasicRealisation extends AbstractRedisMapAdapter{
    public RedisMapAdapterBasicRealisation(Jedis jedis) {
        super(jedis);
    }
}
