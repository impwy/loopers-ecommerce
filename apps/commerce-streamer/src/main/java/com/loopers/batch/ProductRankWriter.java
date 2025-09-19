package com.loopers.batch;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MvProductRankMonthlyRepository;
import com.loopers.application.required.MvProductRankWeeklyRepository;
import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.MvProductRankWeekly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankWriter {
    private final MvProductRankWeeklyRepository mvProductRankWeeklyRepository;
    private final MvProductRankMonthlyRepository mvProductRankMonthlyRepository;

    public void writeWeekly(List<MvProductRankWeekly> rankings) {
        mvProductRankWeeklyRepository.saveAll(rankings);
    }

    public void writeMonthly(List<MvProductRankMonthly> rankings) {
        mvProductRankMonthlyRepository.saveAll(rankings);
    }
}
