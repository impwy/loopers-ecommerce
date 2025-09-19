package com.loopers.batch.infrastructure;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.batch.application.required.MvProductRankMonthlyRepository;
import com.loopers.batch.application.required.MvProductRankWeeklyRepository;
import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.domain.MvProductRankWeekly;

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
