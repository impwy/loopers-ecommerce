package com.loopers.infrastructure.rank;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.ranking.MvProductRankWeekly;

public interface MvProductRankWeeklyJpaRepository extends JpaRepository<MvProductRankWeekly, Long> {
}
