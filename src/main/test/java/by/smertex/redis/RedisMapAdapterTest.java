package by.smertex.redis;

import by.smertex.redis.adapter.interfaces.RedisMapAdapter;
import by.smertex.redis.adapter.realisation.RedisMapAdapterBasicRealisation;
import by.smertex.redis.adapter.realisation.RedisMapAdapterClosableWrapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class RedisMapAdapterTest {

    private static final String HOST_TEST = "127.0.0.1";

    private static final int PORT_TEST = 6379;

    private static final String KEY_TEST = "testKey";

    private static final String VALUE_TEST = "testValue";

    private static final int RANGE_TEST = 100;

    @Test
    void connectionTest() {
        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            assert redisMapAdapter.isConnected();
        }
    }

    @Test
    void putTest(){
        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            redisMapAdapter.put(KEY_TEST, VALUE_TEST);
            assert redisMapAdapter.get(KEY_TEST).equals(VALUE_TEST);
        }
    }

    @Test
    void containsKeyTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            assert redisMapAdapter.containsKey(KEY_TEST);
        }
    }

    @Test
    void containsValueTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                            .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            assert redisMapAdapter.containsValue(VALUE_TEST);
        }
    }

    @Test
    void removeTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            assert redisMapAdapter.remove(KEY_TEST).equals(VALUE_TEST);
            assert redisMapAdapter.get(KEY_TEST) == null;
        }
    }

    @Test
    void putAllTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        Map<String, String> map = new HashMap<>();
        IntStream.range(0, RANGE_TEST + 1)
                .forEach(i -> map.put(keyTest.get(i), valueTest.get(i)));

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            redisMapAdapter.putAll(map);
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> {
                        assert redisMapAdapter.containsKey(keyTest.get(i));
                        assert redisMapAdapter.containsValue(valueTest.get(i));
                    });
        }
    }

    @Test
    void keySetTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            keyTest.forEach(k -> {
                assert redisMapAdapter.containsKey(k);
            });
        }
    }

    @Test
    void valuesTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            valueTest.forEach(v -> {
                assert redisMapAdapter.containsValue(v);
            });
        }
    }

    @Test
    void entrySetTest(){
        List<String> keyTest = new ArrayList<>();
        List<String> valueTest = new ArrayList<>();
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> keyTest.add(i + "a"));
        IntStream.range(0, RANGE_TEST)
                .forEach(i -> valueTest.add(i + "b"));
        keyTest.add(KEY_TEST);
        valueTest.add(VALUE_TEST);

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            IntStream.range(0, RANGE_TEST + 1)
                    .forEach(i -> redisMapAdapter.put(keyTest.get(i), valueTest.get(i)));
            redisMapAdapter.forEach((key, value) -> {
                assert keyTest.contains(key);
                assert valueTest.contains(value);
            });
        }
    }

    @Test
    void entryChangeValueTest(){
        String changeValue = "change";

        try(RedisMapAdapterClosableWrapper redisMapAdapter = new RedisMapAdapterClosableWrapper(HOST_TEST, PORT_TEST)) {
            redisMapAdapter.put(KEY_TEST, VALUE_TEST);
            redisMapAdapter.entrySet().forEach(entry -> entry.setValue(changeValue));
            assert redisMapAdapter.containsValue(changeValue);
        }
    }
}
