package com.loopers.application.required;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.fasterxml.jackson.core.type.TypeReference;

public interface InMemoryRepository {
    void save(String key, Object value);

    void save(String key, Object value, Duration ttl);

    <T> Optional<T> get(String key, TypeReference<T> typeRef);

    void delete(String key);

    <T> List<T> multiGet(List<String> keys, Class<T> type);

    void addProductRanks(String key, Set<Tuple> rankingTuples, Duration ttl);

    Set<TypedTuple<Object>> getProductRanks(String key);

    void saveZset(String key, String value, Double score, Duration ttl);
}
