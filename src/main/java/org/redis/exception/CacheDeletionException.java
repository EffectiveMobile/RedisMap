package org.redis.exception;

public class CacheDeletionException extends RuntimeException {
    public CacheDeletionException(String message) {
        super(message);
    }

}