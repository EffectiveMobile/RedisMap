package org.redis.util.builder;

import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import org.redis.dto.RedisMapEntryDto;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for building predefined Redis map data.
 */
@UtilityClass
public class RedisMapDataBuilder {
    private static final Faker FAKER = new Faker();
    private static final int MIN_KEY_TYPE = 1;
    private static final int MAX_KEY_TYPE = 5;
    private static final int SESSION_KEY_LENGTH = 8;
    private static final int ORDER_ID_LENGTH = 4;
    private static final int INVOICE_ID_LENGTH = 5;

    /**
     * Builds a list of predefined Redis map entries.
     *
     * @param count number of entries to generate.
     * @return list of RedisMapEntryDto objects.
     */
    public static List<RedisMapEntryDto> buildData(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> RedisMapEntryDto.builder()
                        .key(generateKey(i))
                        .value(FAKER.lorem().word())
                        .build())
                .toList();
    }

    /**
     * Generates a Redis key based on a random key type.
     *
     * @param index sequential number for unique key generation.
     * @return generated Redis key as a string.
     */
    private static String generateKey(int index) {
        int keyType = FAKER.number().numberBetween(MIN_KEY_TYPE, MAX_KEY_TYPE);
        return switch (keyType) {
            case 1 -> "user:" + index;
            case 2 -> "session:" + FAKER.regexify("[a-z0-9]{" + SESSION_KEY_LENGTH + "}");
            case 3 -> "order:" + FAKER.number().digits(ORDER_ID_LENGTH);
            case 4 -> "invoice:" + FAKER.number().digits(INVOICE_ID_LENGTH);
            default -> "misc:" + index;
        };
    }
}
