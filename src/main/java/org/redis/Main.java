package org.redis;

public class Main {
    public static void main(String[] args) {
        RedisMap map1 = new RedisMap("map1");
        RedisMap map2 = new RedisMap("map2");

        map1.put("key1", "value1");
        map1.put("key2", "value2");

        map2.put("key1", "value1");
        map2.put("key3", "value3");
        map2.put("key2", "value2");

        System.out.println(map1.size() + " " + map2.size());

        System.out.println(map1.get("key1"));
        System.out.println(map1.get("key2"));
        System.out.println(map2.get("key1"));
        System.out.println(map2.get("key2"));
        System.out.println(map2.get("key3"));

        System.out.println(map1.containsKey("key1"));
        System.out.println(map1.containsKey("key12"));

        System.out.println(map1.entrySet());
        System.out.println(map2.entrySet());
        System.out.println(map1.keySet());
        System.out.println(map2.keySet());

        System.out.println(map1.values());
        System.out.println(map2.values());

        System.out.println(map2.isEmpty());
        System.out.println(map1.remove("key1"));
        System.out.println(map1.size());

        map2.clear();
        System.out.println(map2.size());
    }
}