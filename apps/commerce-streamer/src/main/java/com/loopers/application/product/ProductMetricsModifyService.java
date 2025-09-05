package com.loopers.application.product;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductMetricsRegister;
import com.loopers.application.required.ProductMetricsRepository;
import com.loopers.domain.product.CreateProductMetricsSpec;
import com.loopers.domain.product.ProductMetrics;
import com.loopers.interfaces.consumer.dto.ProductPayload;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMetricsModifyService implements ProductMetricsRegister {
    private final ProductMetricsRepository metricsRepository;
    private final RedisTemplate<String, List<String>> redisTemplate;
    private static final String REDIS_KEY = "product:eventIds";

    @Transactional
    @Override
    public void handleProductEvent(List<ProductPayload> productPayloads) {
        List<String> processedEventIds = redisTemplate.opsForValue().get(REDIS_KEY);
        if (processedEventIds == null) {
            processedEventIds = new ArrayList<>();
        }

        List<ProductMetrics> metricsToSave = new ArrayList<>();
        List<String> newProcessedEventIds = new ArrayList<>();

        for (ProductPayload payload : productPayloads) {
            if (processedEventIds.contains(payload.eventId())) {
                continue;
            }

            ProductMetrics metrics = metricsRepository
                    .findByProductIdAndPublishedAt(payload.productId(), payload.publishedAt().toLocalDate())
                    .orElse(ProductMetrics.create(new CreateProductMetricsSpec(
                            payload.eventId(),
                            payload.productId(),
                            payload.publishedAt().toLocalDate()
                    )));

            switch (payload.eventType()) {
                case PRODUCT_LIKE_INCREMENT -> metrics.increaseLikes();
                case PRODUCT_LIKE_DECREMENT -> metrics.decreaseLikes();
                case PRODUCT_SALE -> metrics.increaseSales(payload.saleQuantity());
                case PRODUCT_SALE_CANCEL -> metrics.decreaseSales(payload.cancelQuantity());
                case PRODUCT_VIEW -> metrics.increaseViews();
            }

            metricsToSave.add(metrics);
            newProcessedEventIds.add(payload.eventId());
        }

        metricsRepository.saveAll(metricsToSave);

        processedEventIds.addAll(newProcessedEventIds);
        redisTemplate.opsForValue().set(REDIS_KEY, processedEventIds, Duration.ofSeconds(3));
    }
}
