package com.loopers.infrastructure;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loopers.application.required.ProductMetricsRepository;
import com.loopers.domain.ProductMetrics;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {
    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public Optional<ProductMetrics> findByProductIdAndPublishedAt(Long productId, LocalDate date) {
        return productMetricsJpaRepository.findByProductIdAndPublishedAt(productId, date);
    }

    @Override
    public void saveAll(Collection<ProductMetrics> productMetrics) {
        productMetricsJpaRepository.saveAll(productMetrics);
    }
}
