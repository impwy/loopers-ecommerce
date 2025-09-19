package com.loopers.infrastructure.rank;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MvProductRankDailyRepository;
import com.loopers.domain.ranking.MvProductRankDaily;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MvProductRankDailyRepositoryImpl implements MvProductRankDailyRepository {
    private final MvProductRankDailyJpaRepository mvProductRankDailyJpaRepository;

    @Override
    public List<MvProductRankDaily> saveAll(List<MvProductRankDaily> rankings) {
        return mvProductRankDailyJpaRepository.saveAll(rankings);
    }

    public List<MvProductRankDaily> findAllByBetweenDate(LocalDate startDate, LocalDate endDate) {
        return mvProductRankDailyJpaRepository.findAllByIssuedDateBetween(startDate, endDate);
    }
}
