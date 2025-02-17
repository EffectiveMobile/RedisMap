package org.redis;

import lombok.extern.slf4j.Slf4j;
import org.redis.dto.RedisMapEntryDto;
import org.redis.exception.handler.RedisMapErrorHandler;
import org.redis.repository.RedisMapRepository;
import org.redis.service.Impl.RedisMapServiceImpl;
import org.redis.service.RedisMapService;
import org.redis.util.builder.RedisMapDataBuilder;
import org.redis.validator.RedisMapKeyValidator;
import org.redis.validator.RedisMapValueValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main class for testing RedisMap functionality.
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        var errorHandler = new RedisMapErrorHandler();
        var repository = new RedisMapRepository(errorHandler);
        RedisMapService service = new RedisMapServiceImpl(repository);

        List<RedisMapEntryDto> testData = RedisMapDataBuilder.buildData();

        restoreTestDataIfEmpty(service, testData);
        checkPut(service, testData);
        checkSizeAndIsEmptyAfterPut(service);

        restoreTestDataIfEmpty(service, testData);
        checkContainsKey(service, testData);
        checkContainsValue(service, testData);

        restoreTestDataIfEmpty(service, testData);
        checkGet(service, testData);
        checkKeySet(service);
        checkValues(service);
        checkEntrySet(service);

        restoreTestDataIfEmpty(service, testData);
        checkPutAll(service, testData);
        checkSizeAfterPutAll(service);

        restoreTestDataIfEmpty(service, testData);
        checkRemove(service, testData);
        checkSizeAndIsEmptyAfterRemove(service);

        restoreTestDataIfEmpty(service, testData);
        checkClear(service);
        checkSizeAndIsEmptyAfterClear(service);
    }

    private static void restoreTestDataIfEmpty(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        if (redisMapService.isEmpty()) {
            testData.forEach(entry -> redisMapService.put(entry.getKey(), entry.getValue()));
            log.info("Test data restored.");
        }
    }

    private static void checkPut(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        testData.forEach(entry -> {
            RedisMapKeyValidator.validateKeyNotNullOrEmpty(entry.getKey());
            RedisMapValueValidator.validateValueNotNullOrEmpty(entry.getValue());
            String oldValue = redisMapService.put(entry.getKey(), entry.getValue());
            log.info("Added/Updated key: '{}', value: '{}'. Old value: '{}'", entry.getKey(), entry.getValue(),
                    oldValue);
        });
    }

    private static void checkSizeAndIsEmptyAfterPut(RedisMapService redisMapService) {
        log.info("Size after put: {}", redisMapService.size());
        log.info("Is empty? {}", redisMapService.isEmpty());
    }

    private static void checkContainsKey(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        testData.forEach(entry -> {
            RedisMapKeyValidator.validateKeyNotNullOrEmpty(entry.getKey());
            log.info("Contains key '{}': {}", entry.getKey(), redisMapService.containsKey(entry.getKey()));
        });
    }

    private static void checkContainsValue(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        testData.forEach(entry -> log.info("Contains value '{}': {}", entry.getValue(),
                redisMapService.containsValue(entry.getValue())));
    }

    private static void checkGet(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        testData.forEach(entry -> {
            RedisMapKeyValidator.validateKeyNotNullOrEmpty(entry.getKey());
            log.info("Value for key '{}': '{}'", entry.getKey(), redisMapService.get(entry.getKey()));
        });
    }

    private static void checkKeySet(RedisMapService redisMapService) {
        log.info("Keys: {}", redisMapService.keySet());
    }

    private static void checkValues(RedisMapService redisMapService) {
        log.info("Values: {}", redisMapService.values());
    }

    private static void checkEntrySet(RedisMapService redisMapService) {
        log.info("Entries: {}", redisMapService.entrySet());
    }

    private static void checkPutAll(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        Map<String, String> testMap = testData.stream()
                .collect(Collectors.toMap(RedisMapEntryDto::getKey, RedisMapEntryDto::getValue));

        testMap.forEach((key, value) -> {
            RedisMapKeyValidator.validateKeyNotNullOrEmpty(key);
            RedisMapValueValidator.validateValueNotNullOrEmpty(value);
        });

        redisMapService.putAll(testMap);
        log.info("Data inserted using putAll: {}", testMap);
    }

    private static void checkSizeAfterPutAll(RedisMapService redisMapService) {
        log.info("Size after putAll: {}", redisMapService.size());
    }

    private static void checkRemove(RedisMapService redisMapService, List<RedisMapEntryDto> testData) {
        testData.forEach(entry -> {
            RedisMapKeyValidator.validateKeyNotNullOrEmpty(entry.getKey());
            log.info("Removed key: '{}', value: '{}'", entry.getKey(), redisMapService.remove(entry.getKey()));
        });
    }

    private static void checkSizeAndIsEmptyAfterRemove(RedisMapService redisMapService) {
        log.info("Size after remove: {}", redisMapService.size());
        log.info("Is empty? {}", redisMapService.isEmpty());
    }

    private static void checkClear(RedisMapService redisMapService) {
        redisMapService.clear();
        log.info("Map cleared");
    }

    private static void checkSizeAndIsEmptyAfterClear(RedisMapService redisMapService) {
        log.info("Size after clear: {}", redisMapService.size());
        log.info("Is empty? {}", redisMapService.isEmpty());
    }
}