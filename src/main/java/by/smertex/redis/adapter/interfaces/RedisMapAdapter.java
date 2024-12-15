package by.smertex.redis.adapter.interfaces;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

public interface RedisMapAdapter extends Map<String, String> {

    long redisSize();

    Jedis getResource();

    JedisPool getJedisPool();
}
