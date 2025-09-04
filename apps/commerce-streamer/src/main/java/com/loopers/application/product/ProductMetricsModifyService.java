package com.loopers.application.product;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductMetricsRegister;
import com.loopers.application.required.ProductMetricsRepository;
import com.loopers.domain.ProductMetrics;
import com.loopers.domain.product.CreateProductMetricsSpec;
import com.loopers.interfaces.consumer.dto.ProductPayload;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMetricsModifyService implements ProductMetricsRegister {
    private final ProductMetricsRepository metricsRepository;

    @Transactional
    @Override
    public void dailyUpdates(List<ProductPayload> productPayloads) {
        Map<String, ProductMetrics> dailyUpdates = new HashMap<>();

        for (ProductPayload event : productPayloads) {
            ZonedDateTime publishedAt = event.publishedAt();
            LocalDate date = publishedAt.toLocalDate();

            String key = event.productId() + "-" + date;
            ProductMetrics metrics = dailyUpdates.computeIfAbsent(key, k ->
                    metricsRepository.findByProductIdAndPublishedAt(event.productId(), date)
                                     .orElse(ProductMetrics.create(new CreateProductMetricsSpec(
                                             event.productId(), date)))
            );

            switch (event.eventType()) {
                case PRODUCT_LIKE_INCREMENT -> metrics.increaseLikes();
                case PRODUCT_LIKE_DECREMENT -> metrics.decreaseLikes();
                case PRODUCT_SALE -> metrics.increaseSales();
                case PRODUCT_VIEW -> metrics.increaseViews();
            }
        }

        metricsRepository.saveAll(dailyUpdates.values());
    }
}
