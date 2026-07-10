package com.loopers.batch.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.batch.domain.MvProductRankMonthly;

public interface MvProductRankMonthlyJpaRepository extends JpaRepository<MvProductRankMonthly, Long> {
    List<MvProductRankMonthly> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
