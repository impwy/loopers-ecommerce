package com.loopers.application.provided;

import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxStatus;

public interface ProductOutboxRegister {
    ProductEventOutbox register(CreateProductOutbox createProductOutbox);

    ProductEventOutbox changeStatus(Long id, ProductOutboxStatus productOutboxStatus);

    ProductEventOutbox updatePayload(Long id, String payload);
}
