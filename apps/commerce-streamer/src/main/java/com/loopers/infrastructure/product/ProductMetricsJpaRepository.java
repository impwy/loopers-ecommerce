package com.loopers.infrastructure.product;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.product.ProductMetrics;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, String> {
    Optional<ProductMetrics> findByProductIdAndPublishedAt(Long productId, LocalDate date);
}
