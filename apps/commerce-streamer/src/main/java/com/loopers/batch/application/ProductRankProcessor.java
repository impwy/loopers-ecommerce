package com.loopers.batch.application;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public ItemProcessor<MvProductRankDaily, MvProductRankMonthly> processMonthly(LocalDate startDate, LocalDate endDate) {
        Map<Long, Double> scoreMap = new HashMap<>();
        AtomicInteger rankCounter = new AtomicInteger(1);

        return daily -> {
            scoreMap.merge(daily.getProductId(), daily.getScore(), Double::sum);
            return MvProductRankMonthly.create(
                    daily.getProductId(),
                    scoreMap.get(daily.getProductId()),
                    rankCounter.getAndIncrement(),
                    startDate,
                    endDate
            );
        };
    }
}
