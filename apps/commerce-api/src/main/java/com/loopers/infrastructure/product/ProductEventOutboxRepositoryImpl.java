package com.loopers.infrastructure.product;

import org.springframework.stereotype.Component;

import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventOutboxRepositoryImpl implements ProductEventOutboxRepository {
    private final ProductEventOutboxJpaRepository productOutboxRepository;

    @Override
    public ProductEventOutbox save(CreateProductOutbox createProductOutbox) {
        ProductEventOutbox productEventOutbox = ProductEventOutbox.create(createProductOutbox);

        return productOutboxRepository.save(productEventOutbox);
    }
}
