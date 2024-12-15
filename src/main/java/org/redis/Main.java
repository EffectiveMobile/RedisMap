package org.redis;

import lombok.RequiredArgsConstructor;
import org.redis.service.MapIntoRedis;

@RequiredArgsConstructor
public class Main {

    private static final MapIntoRedis mapIntoRedis = new MapIntoRedis();

    public static void main(String[] args) {

        try {
            mapIntoRedis.pingRedis();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}