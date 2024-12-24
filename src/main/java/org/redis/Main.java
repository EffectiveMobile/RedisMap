package org.redis;

import lombok.RequiredArgsConstructor;
import org.redis.service.MapIntoRedis;

@RequiredArgsConstructor
public class Main {

    private static final MapIntoRedis mapIntoRedis = new MapIntoRedis();

    public static void main(String[] args) {

        try {
            mapIntoRedis.put("neo", "max");
          //  System.out.println(mapIntoRedis.get("neo").equals("max"));

            System.out.println("Key set" + mapIntoRedis.keySet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}