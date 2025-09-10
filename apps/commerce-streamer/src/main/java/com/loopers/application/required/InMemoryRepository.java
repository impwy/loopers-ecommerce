package com.loopers.application.required;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

public interface InMemoryRepository {
    void save(String key, Object value);

    void save(String key, Object value, Duration ttl);

    <T> Optional<T> get(String key, TypeReference<T> typeRef);

    void delete(String key);

    <T> List<T> multiGet(List<String> keys, Class<T> type);
}
