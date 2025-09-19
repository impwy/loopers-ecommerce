package com.loopers.application.ranking;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductRankingRegister;
import com.loopers.application.required.InMemoryRepository;
import com.loopers.application.required.MvProductRankDailyRepository;
import com.loopers.domain.ranking.MvProductRankDaily;
import com.loopers.interfaces.consumer.dto.ProductPayload;
import com.loopers.interfaces.consumer.dto.ProductPayload.ProductEventType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankingModifyService implements ProductRankingRegister {
    private final InMemoryRepository inMemoryRepository;
    private final MvProductRankDailyRepository mvProductRankDailyRepository;
    private static final Function<String, String> KEY_GENERATOR = key -> "ranking:all:" + key;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void aggregateRanking(List<ProductPayload> productPayloads) {
        LocalDate now = LocalDate.now();
        String key = KEY_GENERATOR.apply(now.format(formatter));
        Map<Long, Double> scoreByProduct = productPayloads.stream()
                                                          .collect(Collectors.groupingBy(
                                                                  ProductPayload::productId,
                                                                  Collectors.summingDouble(this::calculateScore)
                                                          ));

        Set<Tuple> productRankingTuples = scoreByProduct.entrySet().stream()
                                                        .map(e -> Tuple.of(
                                                                String.valueOf(e.getKey()).getBytes(StandardCharsets.UTF_8),
                                                                e.getValue()
                                                        ))
                                                        .collect(Collectors.toSet());
        inMemoryRepository.addProductRanks(key, productRankingTuples, Duration.ofDays(2));

        AtomicInteger rank = new AtomicInteger(1);
        List<MvProductRankDaily> rankings = scoreByProduct.entrySet().stream()
                                                          .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                                                          .map(e -> MvProductRankDaily.create(
                                                                 e.getKey(),
                                                                 now,
                                                                 e.getValue(),
                                                                 rank.getAndIncrement()
                                                         ))
                                                          .toList();

        mvProductRankDailyRepository.saveAll(rankings);
    }

    @Override
    public void prepareNextDayProductRanking() {
        LocalDate now = LocalDate.now();
        String key = KEY_GENERATOR.apply(now.minusDays(1).format(formatter));
        Set<TypedTuple<Object>> typedTuples = inMemoryRepository.getProductRanks(key);

        Set<Tuple> nextDayTuples = typedTuples.stream()
                                              .map(t -> Tuple.of(
                                                      String.valueOf(t.getValue()).getBytes(StandardCharsets.UTF_8),
                                                      t.getScore() * 0.1
                                              ))
                                              .collect(Collectors.toSet());

        inMemoryRepository.addProductRanks(key, nextDayTuples, Duration.ofDays(2));
    }

    private double calculateScore(ProductPayload payload) {
        return ProductEventType.valueOf(payload.eventType().name())
                               .calculateScore(payload);
    }
}
