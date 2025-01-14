package by.smertex.redis.adapter.interfaces;

import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.util.Map;

public interface RedisMapAdapter extends Map<String, String>, Closeable {

    boolean isConnected();

    long redisSize();

    Jedis getJedis();
}
