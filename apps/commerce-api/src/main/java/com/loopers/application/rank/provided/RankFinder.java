package com.loopers.application.rank.provided;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loopers.application.rank.RankingCriteria;
import com.loopers.domain.product.ProductInfo;

public interface RankFinder {
    Page<ProductInfo> getDailyRanking(LocalDate date, Pageable pageable);

    Page<ProductInfo> getWeeklyRanking(LocalDate date, Pageable pageable);

    Page<ProductInfo> getMonthlyRanking(LocalDate date, Pageable pageable);

    Page<ProductInfo> getDefaultRank(Pageable pageable);

    Page<ProductInfo> findProductRanking(RankingCriteria rankingCriteria);
}
