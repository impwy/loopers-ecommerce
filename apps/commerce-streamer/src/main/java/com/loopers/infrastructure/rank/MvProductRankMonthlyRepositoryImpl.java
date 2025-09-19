package com.loopers.infrastructure.rank;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MvProductRankMonthlyRepository;
import com.loopers.application.required.MvProductRankWeeklyRepository;
import com.loopers.domain.ranking.MvProductRankMonthly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MvProductRankMonthlyRepositoryImpl implements MvProductRankMonthlyRepository {
    private final MvProductRankMonthlyJpaRepository mvProductRankMonthlyJpaRepository;

    @Override
    public void saveAll(List<MvProductRankMonthly> rankings) {
        mvProductRankMonthlyJpaRepository.saveAll(rankings);
    }
}
