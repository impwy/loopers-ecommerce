package com.loopers.application.rank.provided;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

public interface RankFinder {
    ProductInfoPageResponse getDailyRanking(LocalDate date, Pageable pageable);

    ProductInfoPageResponse getWeeklyRanking(LocalDate date, Pageable pageable);

    ProductInfoPageResponse getMonthlyRanking(LocalDate date, Pageable pageable);

    ProductInfoPageResponse getDefaultRank(Pageable pageable);
}
