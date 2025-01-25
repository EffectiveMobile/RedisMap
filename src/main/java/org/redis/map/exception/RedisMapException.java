package org.redis.map.exception;

public class RedisMapException extends RuntimeException {
    public RedisMapException() {
        super();
    }

    public RedisMapException(String message) {
        super(message);
    }
}
