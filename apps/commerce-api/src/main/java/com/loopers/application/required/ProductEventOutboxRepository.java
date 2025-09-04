package com.loopers.application.required;

import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductEventOutboxRepository {
    ProductEventOutbox save(CreateProductOutbox createProductOutbox);
}
