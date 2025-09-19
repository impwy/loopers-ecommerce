package com.loopers.batch.infrastructure;

import org.springframework.stereotype.Component;

import com.loopers.batch.application.required.MvProductRankWeeklyRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MvProductRankWeeklyRepositoryImpl implements MvProductRankWeeklyRepository {
    private final MvProductRankWeeklyJpaRepository mvProductRankWeeklyJpaRepository;
}
