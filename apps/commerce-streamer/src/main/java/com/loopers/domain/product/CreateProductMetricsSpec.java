package com.loopers.domain.product;

import java.time.LocalDate;

public record CreateProductMetricsSpec(String eventId, Long productId, LocalDate date) {
}
