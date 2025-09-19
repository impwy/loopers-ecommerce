package com.loopers.application.required;

import java.time.LocalDate;
import java.util.List;

import com.loopers.domain.ranking.MvProductRankDaily;

public interface MvProductRankDailyRepository {
    List<MvProductRankDaily> saveAll(List<MvProductRankDaily> rankings);

    List<MvProductRankDaily> findAllByBetweenDate(LocalDate startDate, LocalDate endDate);
}
