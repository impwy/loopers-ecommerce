package com.loopers.batch.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loopers.domain.ranking.MvProductRankDaily;
import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.domain.MvProductRankWeekly;

@Component
public class ProductRankProcessor {
    public List<MvProductRankWeekly> processWeekly(List<MvProductRankDaily> dailyRankings,
                                                   LocalDate startDate, LocalDate endDate) {
        AtomicInteger rankCounter = new AtomicInteger(1);

        return dailyRankings.stream()
                            .collect(Collectors.groupingBy(MvProductRankDaily::getProductId,
                                                           Collectors.summingDouble(MvProductRankDaily::getScore)))
                            .entrySet().stream()
                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                         .map(e -> MvProductRankWeekly.create(
                                 e.getKey(),
                                 e.getValue(),
                                 rankCounter.getAndIncrement(),
                                 startDate,
                                 endDate
                         )).toList();
    }

    public List<MvProductRankMonthly> processMonthly(List<MvProductRankDaily> dailyRanks, LocalDate startDate, LocalDate endDate) {
        AtomicInteger rankCounter = new AtomicInteger(1);

        return dailyRanks.stream()
                         .collect(Collectors.groupingBy(MvProductRankDaily::getProductId,
                                                        Collectors.summingDouble(MvProductRankDaily::getScore)))
                         .entrySet().stream()
                         .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                         .map(e ->MvProductRankMonthly.create(
                                 e.getKey(),
                                 e.getValue(),
                                 rankCounter.getAndIncrement(),
                                 startDate,
                                 endDate
                         )).toList();
    }
}
