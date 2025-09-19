package com.loopers.batch.application.required;

import java.time.LocalDate;
import java.util.List;

import com.loopers.batch.domain.MvProductRankWeekly;

public interface MvProductRankWeeklyRepository {
    List<MvProductRankWeekly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);

    List<MvProductRankWeekly> findAll();
}
