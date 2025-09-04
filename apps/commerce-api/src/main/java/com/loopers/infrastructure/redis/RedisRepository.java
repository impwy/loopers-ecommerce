package com.loopers.infrastructure.redis;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void save(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public <T> Optional<T> get(String key, TypeReference<T> typeRef) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof String str) {
            try {
                return Optional.of(objectMapper.readValue(str, typeRef));
            } catch (Exception e) {
                throw new RuntimeException("Redis deserialization failed", e);
            }
        }
        return Optional.of((T) value);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public <T> List<T> multiGet(List<String> keys, Class<T> type) {
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);
        if (objects == null || objects.size() == 0) {
            return Collections.emptyList();
        }
        return objects
                .stream().map(type::cast)
                .toList();
    }
}
