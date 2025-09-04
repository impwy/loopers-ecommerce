package com.loopers.infrastructure;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.ProductMetrics;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, Long> {
    Optional<ProductMetrics> findByProductIdAndPublishedAt(Long productId, LocalDate date);
}
