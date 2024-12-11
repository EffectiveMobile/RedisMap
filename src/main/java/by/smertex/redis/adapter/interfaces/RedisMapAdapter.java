package by.smertex.redis.adapter.interfaces;

import java.io.Closeable;
import java.util.Map;

public interface RedisMapAdapter extends Map<String, String>, Closeable {
    boolean isConnected();

    long redisSize();
}
