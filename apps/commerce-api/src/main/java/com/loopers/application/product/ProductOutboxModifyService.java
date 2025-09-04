package com.loopers.application.product;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.application.required.ProductEventOutboxRepository;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductOutboxModifyService implements ProductOutboxRegister {
    private final ProductEventOutboxRepository productEventOutboxRepository;

    @Override
    public ProductEventOutbox register(CreateProductOutbox createProductOutbox) {
        return productEventOutboxRepository.save(createProductOutbox);
    }
}
