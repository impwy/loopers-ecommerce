package com.loopers.batch.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.batch.domain.MvProductRankMonthly;

public interface MvProductRankMonthlyJpaRepository extends JpaRepository<MvProductRankMonthly, Long> {
}
