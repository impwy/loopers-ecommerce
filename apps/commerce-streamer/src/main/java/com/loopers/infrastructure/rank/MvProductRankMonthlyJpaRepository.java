package com.loopers.infrastructure.rank;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.ranking.MvProductRankMonthly;

public interface MvProductRankMonthlyJpaRepository extends JpaRepository<MvProductRankMonthly, Long> {
}
