package com.loopers.batch.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.batch.domain.MvProductRankWeekly;

public interface MvProductRankWeeklyJpaRepository extends JpaRepository<MvProductRankWeekly, Long> {
}
