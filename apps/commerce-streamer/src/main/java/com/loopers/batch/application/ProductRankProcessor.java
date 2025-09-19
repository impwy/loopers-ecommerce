package com.loopers.batch.application;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.domain.MvProductRankWeekly;
import com.loopers.domain.ranking.MvProductRankDaily;

@Component
public class ProductRankProcessor {
    public ItemProcessor<MvProductRankDaily, MvProductRankWeekly> processWeekly(LocalDate startDate, LocalDate endDate) {
        Map<Long, Double> scoreMap = new HashMap<>();
        AtomicInteger rankCounter = new AtomicInteger(1);

        return daily -> {
            scoreMap.merge(daily.getProductId(), daily.getScore(), Double::sum);
            return MvProductRankWeekly.create(
                    daily.getProductId(),
                    scoreMap.get(daily.getProductId()),
                    rankCounter.getAndIncrement(),
                    startDate,
                    endDate
            );
        };
    }

    public List<MvProductRankMonthly> processMonthly(List<MvProductRankDaily> dailyRanks, LocalDate startDate,
                                                     LocalDate endDate) {
        AtomicInteger rankCounter = new AtomicInteger(1);

        return dailyRanks.stream()
                         .collect(Collectors.groupingBy(MvProductRankDaily::getProductId,
                                                        Collectors.summingDouble(MvProductRankDaily::getScore)))
                         .entrySet().stream()
                         .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                         .map(e -> MvProductRankMonthly.create(
                                 e.getKey(),
                                 e.getValue(),
                                 rankCounter.getAndIncrement(),
                                 startDate,
                                 endDate
                         )).toList();
    }
}
