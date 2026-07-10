package com.loopers.infrastructure.rank;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.ranking.MvProductRankDaily;

public interface MvProductRankDailyJpaRepository extends JpaRepository<MvProductRankDaily, Long> {
    List<MvProductRankDaily> findAllByIssuedDateBetween(LocalDate startDate, LocalDate endDate);
}
