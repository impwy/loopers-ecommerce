package com.loopers.application.product;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductMetricsFinder;
import com.loopers.application.required.ProductMetricsRepository;
import com.loopers.domain.product.ProductMetrics;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMetricsQueryService implements ProductMetricsFinder {
    private final ProductMetricsRepository productMetricsRepository;

    @Override
    public ProductMetrics findByProductIdAndPublishedAt(Long productId, LocalDate date) {

        return productMetricsRepository.findByProductIdAndPublishedAt(productId, date)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));
    }
}
