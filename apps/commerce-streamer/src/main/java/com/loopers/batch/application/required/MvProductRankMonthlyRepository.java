package com.loopers.batch.application.required;

import java.util.List;

import com.loopers.batch.domain.MvProductRankMonthly;

public interface MvProductRankMonthlyRepository {
    void saveAll(List<MvProductRankMonthly> rankings);
}
