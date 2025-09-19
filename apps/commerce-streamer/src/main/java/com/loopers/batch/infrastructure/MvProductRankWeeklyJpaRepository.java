package com.loopers.batch.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.batch.domain.MvProductRankWeekly;

public interface MvProductRankWeeklyJpaRepository extends JpaRepository<MvProductRankWeekly, Long> {
    List<MvProductRankWeekly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
