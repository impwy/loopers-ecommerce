package com.loopers.domain.product;

import java.time.LocalDate;

public record CreateProductMetricsSpec(Long productId, LocalDate date) {
}
