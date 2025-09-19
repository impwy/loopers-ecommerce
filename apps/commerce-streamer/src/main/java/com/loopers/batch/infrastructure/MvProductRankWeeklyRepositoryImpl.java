package com.loopers.batch.infrastructure;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.batch.application.required.MvProductRankWeeklyRepository;
import com.loopers.batch.domain.MvProductRankWeekly;

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
