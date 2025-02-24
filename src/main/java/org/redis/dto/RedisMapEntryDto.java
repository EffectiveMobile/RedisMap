package org.redis.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a Redis key-value pair.
 * Used to encapsulate data when interacting with Redis.
 */
@Data
@Builder
public class RedisMapEntryDto {
    private String key;
    private String value;
}
