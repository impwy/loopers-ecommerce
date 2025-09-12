package com.loopers.application.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductRankingRegister;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankingScheduler {
    private final ProductRankingRegister productRankingRegister;

    @Scheduled(cron = "0 50 23 * * *")
    public void prepareNextDayProductRanking() {
        productRankingRegister.prepareNextDayProductRanking();
    }
}
