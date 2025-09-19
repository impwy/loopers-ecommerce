package com.loopers.application.required;

import java.util.List;

import com.loopers.domain.ranking.MvProductRankWeekly;

public interface MvProductRankWeeklyRepository {
    void saveAll(List<MvProductRankWeekly> rankings);
}
