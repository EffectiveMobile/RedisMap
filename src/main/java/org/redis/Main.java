package org.redis;

import lombok.RequiredArgsConstructor;
import org.redis.service.MapIntoRedis;

@RequiredArgsConstructor
public class Main {

    private static final MapIntoRedis mapIntoRedis = new MapIntoRedis();

    public static void main(String[] args) {

        try {
            mapIntoRedis.put("neo", "max");
            mapIntoRedis.put("max", "neo");
          //  System.out.println(mapIntoRedis.get("neo").equals("max"));

         //   System.out.println("Key set" + mapIntoRedis.keySet());
            System.out.println("Values " + mapIntoRedis.values());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}