package com.loopers.application.provided;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

public interface RankFinder {
    ProductInfoPageResponse getDailyRanking(Pageable pageable);

    ProductInfoPageResponse getWeeklyRanking(LocalDate date, Pageable pageable);

    ProductInfoPageResponse getMonthlyRanking(LocalDate date, Pageable pageable);

    ProductInfoPageResponse getDefaultRank(Pageable pageable);
}
