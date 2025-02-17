package org.redis.util.builder;

import lombok.experimental.UtilityClass;
import org.redis.dto.RedisMapEntryDto;

import java.util.List;

/**
 * Utility class for building predefined Redis map data.
 */
@UtilityClass
public class RedisMapDataBuilder {

    /**
     * Builds a list of predefined Redis map entries.
     *
     * @return list of RedisMapEntryDto objects.
     */
    public static List<RedisMapEntryDto> buildData() {
        return List.of(
                RedisMapEntryDto.builder()
                        .key("user:1")
                        .value("John Doe")
                        .build(),
                RedisMapEntryDto.builder()
                        .key("user:2")
                        .value("Jane Doe")
                        .build(),
                RedisMapEntryDto.builder()
                        .key("session:xyz123")
                        .value("Active")
                        .build(),
                RedisMapEntryDto.builder()
                        .key("order:1002")
                        .value("Processing")
                        .build(),
                RedisMapEntryDto.builder()
                        .key("invoice:5555")
                        .value("Paid")
                        .build()
        );
    }
}
