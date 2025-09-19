package com.loopers.infrastructure.rank;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.ranking.MvProductRankDaily;

public interface MvProductRankDailyJpaRepository extends JpaRepository<MvProductRankDaily, Long> {
}
