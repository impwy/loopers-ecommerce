package com.loopers.application.required;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import com.loopers.domain.product.ProductMetrics;

public interface ProductMetricsRepository {
    Optional<ProductMetrics> findByProductIdAndPublishedAt(Long productId, LocalDate date);

    void saveAll(Collection<ProductMetrics> values);
}
