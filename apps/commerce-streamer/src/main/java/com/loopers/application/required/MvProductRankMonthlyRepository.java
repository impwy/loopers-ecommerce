package com.loopers.application.required;

import java.util.List;

import com.loopers.domain.ranking.MvProductRankMonthly;

public interface MvProductRankMonthlyRepository {
    void saveAll(List<MvProductRankMonthly> rankings);
}
