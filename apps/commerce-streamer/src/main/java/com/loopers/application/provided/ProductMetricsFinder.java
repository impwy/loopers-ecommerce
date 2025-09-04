package com.loopers.application.provided;

import java.time.LocalDate;

import com.loopers.domain.ProductMetrics;

public interface ProductMetricsFinder {
    ProductMetrics findByProductIdAndPublishedAt(Long productId, LocalDate date);
}
