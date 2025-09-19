package com.loopers.batch.infrastructure;

import java.time.LocalDate;
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
    public List<MvProductRankWeekly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        return mvProductRankWeeklyJpaRepository.findByStartDateAndEndDate(startDate, endDate);
    }
}
