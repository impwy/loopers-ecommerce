package com.loopers.batch.application.required;

import java.util.List;

import com.loopers.batch.domain.MvProductRankWeekly;

public interface MvProductRankWeeklyRepository {
    void saveAll(List<MvProductRankWeekly> rankings);
}
