package com.loopers.domain.ranking;

import java.util.Set;

import org.springframework.data.redis.connection.zset.Tuple;

public record ProductRankingTuple(Set<Tuple> valueSet) {
}
