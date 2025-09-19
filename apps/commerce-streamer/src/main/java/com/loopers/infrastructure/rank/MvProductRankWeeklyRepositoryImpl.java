package com.loopers.infrastructure.rank;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MvProductRankWeeklyRepository;
import com.loopers.domain.ranking.MvProductRankWeekly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MvProductRankWeeklyRepositoryImpl implements MvProductRankWeeklyRepository {
    private final MvProductRankWeeklyJpaRepository mvProductRankWeeklyJpaRepository;

    @Override
    public void saveAll(List<MvProductRankWeekly> rankings) {
        mvProductRankWeeklyJpaRepository.saveAll(rankings);
    }
}
