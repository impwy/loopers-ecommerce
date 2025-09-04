package com.loopers.application.provided;

import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductOutboxRegister {
    ProductEventOutbox register(CreateProductOutbox createProductOutbox);
}
