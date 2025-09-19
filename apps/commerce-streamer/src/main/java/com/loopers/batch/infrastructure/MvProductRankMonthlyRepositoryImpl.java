package com.loopers.batch.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.batch.application.required.MvProductRankMonthlyRepository;
import com.loopers.batch.domain.MvProductRankMonthly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MvProductRankMonthlyRepositoryImpl implements MvProductRankMonthlyRepository {
    private final MvProductRankMonthlyJpaRepository mvProductRankMonthlyJpaRepository;

    @Override
    public void saveAll(List<MvProductRankMonthly> rankings) {
        mvProductRankMonthlyJpaRepository.saveAll(rankings);
    }

    @Override
    public List<MvProductRankMonthly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        return mvProductRankMonthlyJpaRepository.findByStartDateAndEndDate(startDate, endDate);
    }
}
