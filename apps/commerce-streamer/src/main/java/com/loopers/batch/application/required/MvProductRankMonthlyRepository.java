package com.loopers.batch.application.required;

import java.time.LocalDate;
import java.util.List;

import com.loopers.batch.domain.MvProductRankMonthly;

public interface MvProductRankMonthlyRepository {
    void saveAll(List<MvProductRankMonthly> rankings);

    List<MvProductRankMonthly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
